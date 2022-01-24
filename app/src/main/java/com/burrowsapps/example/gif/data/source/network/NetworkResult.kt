package com.burrowsapps.example.gif.data.source.network

import retrofit2.Response

sealed class NetworkResult<T>(
  val data: T? = null,
  val message: String? = null
) {
  class Success<T>(data: T) : NetworkResult<T>(data)
  class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)
  class Loading<T> : NetworkResult<T>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
  try {
    val response = apiCall()
    if (response.isSuccessful) {
      response.body()?.let {
        return NetworkResult.Success(it)
      }
    }
    return error("${response.code()} ${response.message()}")
  } catch (e: Exception) {
    return error(e.message ?: e.toString())
  }
}

private fun <T> error(errorMessage: String): NetworkResult<T> =
  NetworkResult.Error("Api call failed $errorMessage")
