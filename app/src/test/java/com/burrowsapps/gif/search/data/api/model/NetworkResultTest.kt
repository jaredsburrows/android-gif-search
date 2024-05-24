package com.burrowsapps.gif.search.data.api.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.api.model.NetworkResult.Empty
import com.burrowsapps.gif.search.data.api.model.NetworkResult.Error
import com.burrowsapps.gif.search.data.api.model.NetworkResult.Success
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR

@RunWith(AndroidJUnit4::class)
class NetworkResultTest {
  @Test
  fun testSafeApiCallReturnsSuccessWhenResponseIsSuccessfulAndHasBody() {
    val result =
      runBlocking {
        NetworkResult.safeApiCall {
          Response.success("body")
        }
      }

    assertThat(result).isInstanceOf(Success::class.java)
  }

  @Test
  fun testSafeApiCallReturnsEmptyWhenResponseIsSuccessfulAndHasNoBody() {
    val result =
      runBlocking {
        NetworkResult.safeApiCall {
          Response.success(null)
        }
      }

    assertThat(result).isInstanceOf(Empty::class.java)
  }

  @Test
  fun testSafeApiCallReturnsErrorWhenResponseIsNotSuccessful() {
    val result =
      runBlocking {
        NetworkResult.safeApiCall {
          Response.error<String>(HTTP_INTERNAL_ERROR, "error".toResponseBody())
        }
      }

    assertThat(result).isInstanceOf(Error::class.java)
  }

  @Test
  fun testSafeApiCallReturnsErrorWhenAPICallThrowsException() {
    val result =
      runBlocking {
        NetworkResult.safeApiCall<String> {
          throw RuntimeException("API call failed")
        }
      }

    assertThat(result).isInstanceOf(Error::class.java)
  }
}
