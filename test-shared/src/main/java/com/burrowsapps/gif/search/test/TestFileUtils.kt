package com.burrowsapps.gif.search.test

import okhttp3.mockwebserver.MockResponse
import okio.Buffer
import okio.source
import java.net.HttpURLConnection.HTTP_OK

object TestFileUtils {
  const val MOCK_SERVER_PORT = 8_080
  const val MOCK_SERVER_URL = "http://localhost:$MOCK_SERVER_PORT"
  private const val HTTP_200_STATUS = "HTTP/1.1 200 OK"

  fun getMockResponse(fileName: String) =
    MockResponse().apply {
      status = HTTP_200_STATUS
      setResponseCode(code = HTTP_OK)
      setBody(body = readText(fileName = fileName))
      addHeader(header = "Content-type: application/json; charset=utf-8")
    }

  fun getMockGifResponse(fileName: String) =
    getMockFileResponse(fileName).apply {
      addHeader(header = "Content-type: image/gif")
    }

  private fun getMockFileResponse(fileName: String) =
    MockResponse().apply {
      status = HTTP_200_STATUS
      setResponseCode(code = HTTP_OK)
      setBody(body = readFile(fileName = fileName))
    }

  private fun readFile(fileName: String) =
    Buffer().apply {
      openStream(fileName = fileName).source().use { source ->
        writeAll(source = source)
      }
    }

  private fun readText(fileName: String) = getResource(fileName = fileName).readText()

  private fun openStream(fileName: String) = getResource(fileName = fileName).openStream()

  private fun getResource(fileName: String) = TestFileUtils::class.java.getResource(fileName)!!
}
