package com.burrowsapps.example.gif.test

import okhttp3.mockwebserver.MockResponse
import okio.Buffer
import okio.source
import java.net.HttpURLConnection.HTTP_OK

object TestFileUtils {
  const val MOCK_SERVER_PORT = 8_080
  private const val HTTP_200_STATUS = "HTTP/1.1 200 OK"

  fun getMockResponse(fileName: String) = MockResponse().apply {
    status = HTTP_200_STATUS
    setResponseCode(HTTP_OK)
    setBody(readText(fileName))
    addHeader("Content-type: application/json; charset=utf-8")
  }

  fun getMockFileResponse(fileName: String) = MockResponse().apply {
    status = HTTP_200_STATUS
    setResponseCode(HTTP_OK)
    setBody(readImage(fileName))
    addHeader("Content-type: image/webp")
  }

  private fun readImage(fileName: String) = Buffer().apply {
    openStream(fileName).source().use { source ->
      writeAll(source)
    }
  }

  private fun readText(fileName: String) = getResource(fileName).readText()

  private fun openStream(fileName: String) = getResource(fileName).openStream()

  private fun getResource(fileName: String) = TestFileUtils::class.java.getResource(fileName)!!
}
