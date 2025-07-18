package com.burrowsapps.gif.search.data.api.model

import retrofit2.Response
import timber.log.Timber

/**
 * A sealed class that represents the result of a network request.
 *
 * This class has four possible subclasses: Loading, Success, Empty, and Error. The Loading subclass
 * represents that the network request is still in progress, the Success subclass represents that
 * the request completed successfully and returned data, the Empty subclass represents that the
 * request completed successfully but did not return any data, and the Error subclass represents
 * that the request failed due to an error.
 *
 * @property data The data returned by the network request, if any.
 * @property message A message associated with the network result, if any.
 */
internal sealed class NetworkResult<T>(
  val data: T? = null,
  val message: String? = null,
) {
  class Loading<T> : NetworkResult<T>()

  class Success<T>(
    data: T,
  ) : NetworkResult<T>(data = data)

  class Empty<T> : NetworkResult<T>()

  class Error<T>(
    data: T? = null,
    message: String? = null,
  ) : NetworkResult<T>(data = data, message = message)

  companion object {
    /**
     * Executes a suspend function that performs a network request, and returns a NetworkResult
     * object representing the result of the request.
     *
     * This function takes a suspend function that performs a network request using Retrofit, and
     * returns a NetworkResult object representing the result of the request. If the request is
     * successful, the function returns a Success or Empty object depending on whether the response
     * contains data or not. If the request fails, the function returns an Error object with a
     * message describing the error.
     *
     * @param apiCall A suspend function that performs a network request using Retrofit.
     * @return A NetworkResult object representing the result of the network request.
     */
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> =
      runCatching { apiCall() }
        .fold(
          onSuccess = { response ->
            val body = response.body()
            when {
              // Success response with empty body
              response.isSuccessful && body == null -> {
                Timber.i("Request was successful with an empty body")
                Empty()
              }
              // Success response with body
              response.isSuccessful && body != null -> {
                Timber.i("Request was successful with a body")
                Success(data = body)
              }
              else -> {
                // Error response
                Timber.e("Request failed with message: ${response.message()}")
                error(response.message())
              }
            }
          },
          onFailure = { e ->
            // Error response because of exception
            Timber.e(e, "Request failed with exception")
            error(e.message ?: e.toString())
          },
        )

    private fun <T> error(errorMessage: String): NetworkResult<T> = Error(message = errorMessage)
  }
}
