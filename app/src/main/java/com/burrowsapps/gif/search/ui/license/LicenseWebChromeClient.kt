package com.burrowsapps.gif.search.ui.license

import android.webkit.WebChromeClient
import android.webkit.WebView
import timber.log.Timber

class LicenseWebChromeClient : WebChromeClient() {
  override fun onProgressChanged(
    view: WebView?,
    newProgress: Int,
  ) {
    super.onProgressChanged(view, newProgress)

    Timber.tag("LicenseWebChromeClient").i("onProgressChanged: $newProgress")
  }
}
