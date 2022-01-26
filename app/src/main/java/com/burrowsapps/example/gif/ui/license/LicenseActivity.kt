package com.burrowsapps.example.gif.ui.license

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.webkit.WebSettingsCompat.FORCE_DARK_OFF
import androidx.webkit.WebSettingsCompat.FORCE_DARK_ON
import androidx.webkit.WebSettingsCompat.setForceDark
import androidx.webkit.WebViewAssetLoader.AssetsPathHandler
import androidx.webkit.WebViewAssetLoader.Builder
import androidx.webkit.WebViewFeature.FORCE_DARK
import androidx.webkit.WebViewFeature.isFeatureSupported
import com.burrowsapps.example.gif.R
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Open source license activity.
 */
@AndroidEntryPoint
class LicenseActivity : AppCompatActivity() {

  private lateinit var webView: WebView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Instead of loading files using "files://" directly
    val assetLoader = Builder()
      .addPathHandler("/assets/", AssetsPathHandler(this))
      .build()

    webView = WebView(this).apply {
      // Override URLs for AssetsPathHandler
      webViewClient = object : WebViewClient() {
        override fun shouldInterceptRequest(
          view: WebView,
          request: WebResourceRequest
        ): WebResourceResponse? {
          return assetLoader.shouldInterceptRequest(request.url) ?: super.shouldInterceptRequest(
            view,
            request
          )
        }

        override fun onReceivedHttpError(
          view: WebView,
          request: WebResourceRequest,
          errorResponse: WebResourceResponse
        ) {
          Timber.e("onReceivedHttpError:\t$errorResponse")
        }
      }

      settings.apply {
        allowFileAccess = false
        allowContentAccess = false
        setGeolocationEnabled(false)
        @Suppress("DEPRECATION")
        if (SDK_INT < VERSION_CODES.R) {
          allowFileAccessFromFileURLs = false
          allowUniversalAccessFromFileURLs = false
        }

        // Handle dark mode for webview
        if (isFeatureSupported(FORCE_DARK)) {
          when (resources.configuration.uiMode and UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> setForceDark(settings, FORCE_DARK_ON)
            else -> setForceDark(settings, FORCE_DARK_OFF)
          }
        }
      }

      // https://developer.android.com/reference/androidx/webkit/WebViewAssetLoader
      loadUrl("https://appassets.androidplatform.net/assets/open_source_licenses.html")
    }

    setContentView(webView)

    supportActionBar?.title = getString(R.string.menu_licenses)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        when {
          webView.canGoBack() -> webView.goBack()
          else -> super.onBackPressed()
        }
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onBackPressed() {
    when {
      webView.canGoBack() -> webView.goBack()
      else -> super.onBackPressed()
    }
  }

  companion object {
    fun createIntent(context: Context): Intent {
      return Intent().setClass(context, LicenseActivity::class.java)
    }
  }
}
