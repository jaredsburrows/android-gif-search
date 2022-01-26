package com.burrowsapps.example.gif.data.source.network

import retrofit2.Response

sealed class NetworkResult<T>(
  val data: T? = null,
  val message: String? = null
) {
  class Success<T>(data: T) : NetworkResult<T>(data)
  class Error<T>(data: T? = null, message: String) : NetworkResult<T>(data, message)

  companion object {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
      return try {
        val response = apiCall()
        val body = response.body()
        if (response.isSuccessful && body != null) {
          return Success(body)
        }
        error("${response.code()} ${response.message()}")
      } catch (e: Exception) {
        error(e.message ?: e.toString())
      }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
      Error(null, "Api call failed $errorMessage")
  }
}
