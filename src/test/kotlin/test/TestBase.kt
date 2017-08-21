package test

import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Before
import java.io.InputStreamReader
import java.net.HttpURLConnection.HTTP_OK
import java.util.Random
import java.util.UUID.randomUUID

/**
 * JUnit Tests.
 *
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
@Suppress("unused")
abstract class TestBase {
  companion object {
    val MOCK_SERVER_PORT = 8080
    val NUMBER_NEGATIVE_ONE = -1
    val NUMBER_ZERO = 0
    val NUMBER_ONE = 1
    val STRING_EMPTY = ""
    val STRING_NULL: String? = null
    val STRING_UNIQUE = randomUUID().toString()
    val STRING_UNIQUE2 = randomUUID().toString() + randomUUID().toString()
    val STRING_UNIQUE3 = randomUUID().toString()
    val INTEGER_RANDOM: Int = Random().nextInt()
    val INTEGER_RANDOM_POSITIVE: Int = Random().nextInt(Integer.SIZE - 1)
    val LONG_RANDOM: Long = Random().nextLong()
    val INT_RANDOM: Int = Random().nextInt()
    val DOUBLE_RANDOM: Double = Random().nextDouble()

    @JvmStatic fun getMockResponse(fileName: String): MockResponse {
      return MockResponse()
        .setStatus("HTTP/1.1 200")
        .setResponseCode(HTTP_OK)
        .setBody(parseText(fileName))
        .addHeader("Content-type: application/json; charset=utf-8")
    }

    @JvmStatic fun parseText(fileName: String): String {
      val inputStream = TestBase::class.java.getResourceAsStream(fileName)
      val text = InputStreamReader(inputStream).readText()
      inputStream.close()
      return text
    }
  }

  @Before open fun setUp() {
  }

  @After open fun tearDown() {
  }
}
