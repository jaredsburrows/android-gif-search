package com.burrowsapps.example.gif.ui.license

import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.webkit.WebSettingsCompat.FORCE_DARK_OFF
import androidx.webkit.WebSettingsCompat.FORCE_DARK_ON
import androidx.webkit.WebSettingsCompat.setForceDark
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewFeature.FORCE_DARK
import androidx.webkit.WebViewFeature.isFeatureSupported
import com.burrowsapps.example.gif.R
import com.burrowsapps.example.gif.databinding.ActivityLicenseBinding
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.rememberWebViewState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Open source license activity.
 */
@AndroidEntryPoint
class LicenseActivity : AppCompatActivity() {

  private lateinit var binding: ActivityLicenseBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityLicenseBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.toolbar.setTitle(R.string.menu_licenses)
    setSupportActionBar(binding.toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    binding.composeView.setContent {
      TheContent()
    }
  }

  companion object {
    fun createIntent(context: Context): Intent {
      return Intent().setClass(context, LicenseActivity::class.java)
    }
  }
}

@Preview(
  name = "one",
  showBackground = true,
  device = Devices.PIXEL,
  locale = "en",
  showSystemUi = true,
  uiMode = UI_MODE_NIGHT_YES,
)
@Preview(
  name = "two",
  showBackground = true,
  device = Devices.PIXEL,
  locale = "en",
  showSystemUi = true,
  uiMode = UI_MODE_NIGHT_NO,
)
@Composable
private fun DefaultPreview() {
  TheContent()
}

@Composable
private fun TheContent() {
  val context = LocalContext.current
  // https://developer.android.com/reference/androidx/webkit/WebViewAssetLoader
  val state =
    rememberWebViewState("https://appassets.androidplatform.net/assets/open_source_licenses.html")
  val pathHandler = WebViewAssetLoader.AssetsPathHandler(context)
  // Instead of loading files using "files://" directly
  val assetLoader = WebViewAssetLoader.Builder()
    .addPathHandler("/assets/", pathHandler)
    .build()

  com.google.accompanist.web.WebView(
    state,
    captureBackPresses = true,
    onCreated = { webView ->
      webView.isVerticalScrollBarEnabled = false
      webView.settings.apply {
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
          when (webView.resources.configuration.uiMode and UI_MODE_NIGHT_MASK) {
            UI_MODE_NIGHT_YES -> setForceDark(this, FORCE_DARK_ON)
            else -> setForceDark(this, FORCE_DARK_OFF)
          }
        }
      }
    },
    client = object : AccompanistWebViewClient() {
      override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
      ): WebResourceResponse? {
        // Override URLs for AssetsPathHandler
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
    },
  )
}
