package test

import okhttp3.mockwebserver.MockResponse
import okio.Buffer
import okio.Okio
import org.junit.After
import org.junit.Before
import java.io.InputStreamReader
import java.net.HttpURLConnection.HTTP_OK
import java.util.Random
import java.util.UUID.randomUUID

/**
 * JUnit Tests.
 */
@Suppress("unused")
abstract class TestBase {
  companion object {
    const val MOCK_SERVER_PORT = 8080
    const val NUMBER_NEGATIVE_ONE = -1
    const val NUMBER_ZERO = 0
    const val NUMBER_ONE = 1
    const val STRING_EMPTY = ""
    @JvmField val STRING_NULL: String? = null
    @JvmField val STRING_UNIQUE = randomUUID().toString()
    @JvmField val STRING_UNIQUE2 = randomUUID().toString() + randomUUID().toString()
    @JvmField val STRING_UNIQUE3 = randomUUID().toString()
    @JvmField val INTEGER_RANDOM = Random().nextInt()
    @JvmField val INTEGER_RANDOM_POSITIVE = Random().nextInt(Integer.SIZE - 1)
    @JvmField val LONG_RANDOM = Random().nextLong()
    @JvmField val INT_RANDOM = Random().nextInt()
    @JvmField val DOUBLE_RANDOM = Random().nextDouble()

    @JvmStatic fun getMockResponse(fileName: String): MockResponse {
      return MockResponse()
        .setStatus("HTTP/1.1 200")
        .setResponseCode(HTTP_OK)
        .setBody(parseText(fileName))
        .addHeader("Content-type: application/json; charset=utf-8")
    }

    @JvmStatic private fun parseText(fileName: String): String {
      val inputStream = TestBase::class.java.getResourceAsStream(fileName)
      val text = InputStreamReader(inputStream).readText()
      inputStream.close()
      return text
    }

    @JvmStatic fun getMockFileResponse(fileName: String): MockResponse {
      return MockResponse()
        .setStatus("HTTP/1.1 200")
        .setResponseCode(HTTP_OK)
        .setBody(parseImage(fileName))
        .addHeader("content-type: image/png")
    }

    @JvmStatic private fun parseImage(fileName: String): Buffer {
      val inputStream = TestBase::class.java.getResourceAsStream(fileName)
      val source = Okio.source(inputStream)
      val result = Buffer()
      result.writeAll(source)
      inputStream.close()
      source.close()
      return result
    }
  }

  @Before open fun setUp() {
  }

  @After open fun tearDown() {
  }
}
