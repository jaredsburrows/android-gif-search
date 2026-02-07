package com.burrowsapps.gif.search.ui.license

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader
import timber.log.Timber

internal class LicenseWebViewClient(
  private val assetLoader: WebViewAssetLoader,
) : WebViewClient() {
  override fun onPageStarted(
    view: WebView?,
    url: String?,
    favicon: Bitmap?,
  ) {
    super.onPageStarted(view, url, favicon)

    Timber.tag("LicenseWebViewClient").i("onPageStarted: $url")
  }

  override fun onPageFinished(
    view: WebView,
    url: String,
  ) {
    super.onPageFinished(view, url)

    Timber.tag("LicenseWebViewClient").i("onPageFinished: $url")
  }

  override fun onLoadResource(
    view: WebView?,
    url: String?,
  ) {
    super.onLoadResource(view, url)

    Timber.tag("LicenseWebViewClient").i("onLoadResource: $url")
  }

  override fun shouldInterceptRequest(
    view: WebView,
    request: WebResourceRequest,
  ): WebResourceResponse? {
    // Override URLs for AssetsPathHandler
    val response = assetLoader.shouldInterceptRequest(request.url)
    Timber.tag("LicenseWebViewClient").i("shouldInterceptRequest: ${request.url} -> ${response?.statusCode}")
    return response ?: super.shouldInterceptRequest(view, request)
  }

  override fun onReceivedHttpError(
    view: WebView,
    request: WebResourceRequest,
    errorResponse: WebResourceResponse,
  ) {
    Timber.tag("LicenseWebViewClient").e("onReceivedHttpError: ${request.url} (${errorResponse.statusCode})")
  }
}
