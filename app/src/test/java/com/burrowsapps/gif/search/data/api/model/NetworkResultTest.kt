package com.burrowsapps.gif.search.data.api.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.data.api.model.NetworkResult.Companion.safeApiCall
import com.burrowsapps.gif.search.data.api.model.NetworkResult.Empty
import com.burrowsapps.gif.search.data.api.model.NetworkResult.Error
import com.burrowsapps.gif.search.data.api.model.NetworkResult.Success
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Response
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR

@RunWith(AndroidJUnit4::class)
class NetworkResultTest {
  @Test
  fun testSafeApiCallReturnsSuccessWhenResponseIsSuccessfulAndHasBody() =
    runTest {
      val result =
        runBlocking(IO) {
          safeApiCall {
            Response.success("body")
          }
        }

      assertThat(result).isInstanceOf(Success::class.java)
      assertThat((result as Success).data).isEqualTo("body")
    }

  @Test
  fun testSafeApiCallReturnsEmptyWhenResponseIsSuccessfulAndHasNoBody() =
    runTest {
      val result =
        runBlocking(IO) {
          safeApiCall {
            Response.success(null)
          }
        }

      assertThat(result).isInstanceOf(Empty::class.java)
      assertThat((result as Empty).data as Any?).isNull()
    }

  @Test
  fun testSafeApiCallReturnsErrorWhenResponseIsNotSuccessful() =
    runTest {
      val result =
        runBlocking(IO) {
          safeApiCall {
            Response.error<String>(HTTP_INTERNAL_ERROR, "error".toResponseBody())
          }
        }

      assertThat(result).isInstanceOf(Error::class.java)
      assertThat((result as Error).data).isNull()
    }

  @Test
  fun testSafeApiCallReturnsErrorWhenAPICallThrowsException() =
    runTest {
      val result =
        runBlocking(IO) {
          safeApiCall<String> {
            throw RuntimeException("API call failed")
          }
        }

      assertThat(result).isInstanceOf(Error::class.java)
      assertThat((result as Error).data).isNull()
    }
}
