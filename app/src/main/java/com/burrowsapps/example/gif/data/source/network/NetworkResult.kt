package com.burrowsapps.example.gif.data.source.network

import android.util.Log
import android.util.Log.ERROR
import android.util.Log.isLoggable
import retrofit2.Response

sealed class NetworkResult<T>(
  val data: T? = null,
  val message: String? = null
) {
  class Success<T>(data: T) : NetworkResult<T>(data)
  class Error<T>(data: T? = null, message: String) : NetworkResult<T>(data, message)

  companion object {
    private const val TAG = "NetworkResult"

    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
      return try {
        val response = apiCall()
        val body = response.body()
        if (response.isSuccessful && body != null) {
          return Success(body)
        }
        if (isLoggable(TAG, ERROR)) Log.e(TAG, "request failed:\t $response")
        error("${response.code()} ${response.message()}")
      } catch (e: Exception) {
        if (isLoggable(TAG, ERROR)) Log.e(TAG, "request failed", e)
        error(e.message ?: e.toString())
      }
    }

    private fun <T> error(errorMessage: String): NetworkResult<T> =
      Error(null, "Api call failed $errorMessage")
  }
}
