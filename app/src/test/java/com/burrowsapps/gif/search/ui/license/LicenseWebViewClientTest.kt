package com.burrowsapps.gif.search.ui.license

import android.content.Context
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.webkit.WebViewAssetLoader
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LicenseWebViewClientTest {
  private lateinit var client: LicenseWebViewClient
  private lateinit var assetLoader: WebViewAssetLoader
  private lateinit var webView: WebView

  @Before
  fun setUp() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assetLoader =
      WebViewAssetLoader
        .Builder()
        .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
        .build()

    client = LicenseWebViewClient(assetLoader)
    webView = WebView(context)
  }

  @Test
  fun testOnPageStartedDoesNotThrow() {
    client.onPageStarted(webView, "https://example.com", createBitmap(1, 1))
  }

  @Test
  fun testOnPageFinishedDoesNotThrow() {
    client.onPageFinished(webView, "https://example.com")
  }

  @Test
  fun testOnLoadResourceDoesNotThrow() {
    client.onLoadResource(webView, "https://example.com")
  }

  @Test
  fun testOnReceivedHttpErrorDoesNotThrow() {
    val request =
      object : WebResourceRequest {
        override fun getUrl(): Uri = "https://example.com".toUri()

        override fun isForMainFrame(): Boolean = true

        override fun isRedirect(): Boolean = false

        override fun hasGesture(): Boolean = false

        override fun getMethod(): String = "GET"

        override fun getRequestHeaders(): MutableMap<String, String> = mutableMapOf()
      }

    val response = WebResourceResponse("text/plain", "utf-8", 404, "Not Found", mapOf(), null)

    client.onReceivedHttpError(webView, request, response)
  }

  @Test
  fun testShouldInterceptRequestReturnsNullOrAsset() {
    val request =
      object : WebResourceRequest {
        override fun getUrl(): Uri = "https://example.com/assets/something.html".toUri()

        override fun isForMainFrame(): Boolean = true

        override fun isRedirect(): Boolean = false

        override fun hasGesture(): Boolean = false

        override fun getMethod(): String = "GET"

        override fun getRequestHeaders(): MutableMap<String, String> = mutableMapOf()
      }

    // Will be null unless asset actually exists in your test /assets dir
    client.shouldInterceptRequest(webView, request)
  }
}
