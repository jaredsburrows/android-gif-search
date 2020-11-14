package test

import okhttp3.mockwebserver.MockResponse
import okio.Buffer
import okio.source
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object TestFileUtils {
  const val MOCK_SERVER_PORT = 8080
  private const val HTTP_200_STATUS = "HTTP/1.1 200"

  fun getMockResponse(fileName: String): MockResponse = MockResponse().apply {
    status = HTTP_200_STATUS
    setResponseCode(HttpURLConnection.HTTP_OK)
    setBody(readText(fileName))
    addHeader("Content-type: application/json; charset=utf-8")
  }

  fun getMockFileResponse(fileName: String): MockResponse = MockResponse().apply {
    status = HTTP_200_STATUS
    setResponseCode(HttpURLConnection.HTTP_OK)
    setBody(readImage(fileName))
    addHeader("Content-type: image/png")
  }

  private fun readImage(fileName: String): Buffer = Buffer().apply {
    openStream(fileName).source().use { source ->
      writeAll(source)
    }
  }

  private fun readText(fileName: String): String = getResource(fileName).readText()

  private fun openStream(fileName: String): InputStream = getResource(fileName).openStream()

  private fun getResource(fileName: String): URL = TestFileUtils::class.java.getResource(fileName)!!
}
