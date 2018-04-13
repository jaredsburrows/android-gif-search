package test

import okhttp3.mockwebserver.MockResponse
import okio.Buffer
import okio.Okio
import java.io.InputStreamReader
import java.net.HttpURLConnection

object TestUtils {
  const val MOCK_SERVER_PORT = 8080

  @JvmStatic fun getMockResponse(fileName: String): MockResponse {
    return MockResponse()
      .setStatus("HTTP/1.1 200")
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(parseText(fileName))
      .addHeader("Content-type: application/json; charset=utf-8")
  }

  @JvmStatic private fun parseText(fileName: String): String {
    val inputStream = TestUtils::class.java.getResourceAsStream(fileName)
    val text = InputStreamReader(inputStream).readText()
    inputStream.close()
    return text
  }

  @JvmStatic fun getMockFileResponse(fileName: String): MockResponse {
    return MockResponse()
      .setStatus("HTTP/1.1 200")
      .setResponseCode(HttpURLConnection.HTTP_OK)
      .setBody(parseImage(fileName))
      .addHeader("content-type: image/png")
  }

  @JvmStatic private fun parseImage(fileName: String): Buffer {
    val inputStream = TestUtils::class.java.getResourceAsStream(fileName)
    val source = Okio.source(inputStream)
    val result = Buffer()
    result.writeAll(source)
    source.close()
    return result
  }
}
