package com.burrowsapps.gif.search.ui.license

import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader
import timber.log.Timber

class LicenseWebViewClient(
  private val assetLoader: WebViewAssetLoader,
) : WebViewClient() {
  override fun onPageStarted(
    view: WebView?,
    url: String?,
    favicon: Bitmap?,
  ) {
    super.onPageStarted(view, url, favicon)

    Timber.i("onPageStarted:\t$url")
  }

  override fun onPageFinished(
    view: WebView,
    url: String,
  ) {
    super.onPageFinished(view, url)

    Timber.i("onPageFinished:\t$url")
  }

  override fun onLoadResource(
    view: WebView?,
    url: String?,
  ) {
    super.onLoadResource(view, url)

    Timber.i("onLoadResource:\t$url")
  }

  override fun shouldInterceptRequest(
    view: WebView,
    request: WebResourceRequest,
  ): WebResourceResponse? {
    // Override URLs for AssetsPathHandler
    val override = assetLoader.shouldInterceptRequest(request.url)
    Timber.i("shouldInterceptRequest:\t${request.url}\t${override?.statusCode}")
    return override ?: super.shouldInterceptRequest(view, request)
  }

  override fun onReceivedHttpError(
    view: WebView,
    request: WebResourceRequest,
    errorResponse: WebResourceResponse,
  ) {
    Timber.e("onReceivedHttpError:\t${request.url}\t${errorResponse.statusCode}")
  }
}
