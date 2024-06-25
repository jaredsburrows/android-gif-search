@file:OptIn(ExperimentalMaterial3Api::class)

package com.burrowsapps.gif.search.ui.license

import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Devices.PIXEL_7_PRO
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.webkit.WebSettingsCompat.FORCE_DARK_OFF
import androidx.webkit.WebSettingsCompat.FORCE_DARK_ON
import androidx.webkit.WebSettingsCompat.setAlgorithmicDarkeningAllowed
import androidx.webkit.WebSettingsCompat.setForceDark
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewFeature.ALGORITHMIC_DARKENING
import androidx.webkit.WebViewFeature.FORCE_DARK
import androidx.webkit.WebViewFeature.isFeatureSupported
import com.burrowsapps.gif.search.R
import com.burrowsapps.gif.search.ui.theme.GifTheme
import timber.log.Timber

/** Shows the license screen of the app. */
@Preview(
  name = "dark",
  showBackground = true,
  device = PIXEL_7_PRO,
  locale = "en",
  showSystemUi = true,
  uiMode = UI_MODE_NIGHT_YES,
)
@Preview(
  name = "light",
  showBackground = true,
  device = PIXEL_7_PRO,
  locale = "en",
  showSystemUi = true,
  uiMode = UI_MODE_NIGHT_NO,
)
@Composable
private fun DefaultPreview(navController: NavHostController = rememberNavController()) {
  GifTheme {
    LicenseScreen(
      navController = navController,
    )
  }
}

@Composable
internal fun LicenseScreen(
  navController: NavHostController,
  modifier: Modifier = Modifier,
) {
  val scrollBehavior = enterAlwaysScrollBehavior(rememberTopAppBarState())

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = { TheToolbar(navController, scrollBehavior) },
  ) { paddingValues ->
    TheContent(paddingValues)
  }
}

@Composable
private fun TheToolbar(
  navController: NavHostController,
  scrollBehavior: TopAppBarScrollBehavior,
) {
  val context = LocalContext.current

  TopAppBar(
    title = {
      Text(
        modifier = Modifier.semantics { contentDescription = context.getString(R.string.license_screen_content_description) },
        text = stringResource(R.string.license_screen_title),
      )
    },
    navigationIcon = {
      if (navController.previousBackStackEntry != null) {
        IconButton(
          onClick = {
            navController.navigateUp()
          },
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(id = R.string.menu_back_content_description),
          )
        }
      }
    },
    scrollBehavior = scrollBehavior,
  )
}

@Composable
private fun TheContent(innerPadding: PaddingValues) {
  Column(
    modifier =
      Modifier
        .padding(innerPadding)
        .verticalScroll(rememberScrollState()),
  ) {
    // TODO Nested WebView prevent anchor clicks
    TheWebView()
  }
}

@Composable
private fun TheWebView() {
  val context = LocalContext.current
  val runningInPreview = LocalInspectionMode.current

  // https://developer.android.com/reference/androidx/webkit/WebViewAssetLoader
  val pathHandler = WebViewAssetLoader.AssetsPathHandler(context)
  // Instead of loading files using "files://" directly
  val assetLoader = WebViewAssetLoader.Builder().addPathHandler("/assets/", pathHandler).build()

  AndroidView(
    factory = { _ ->
      WebView(context).apply {
        isVerticalScrollBarEnabled = false
        webViewClient =
          object : WebViewClient() {
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
              super.shouldInterceptRequest(view, request)

              // Override URLs for AssetsPathHandler
              val override = assetLoader.shouldInterceptRequest(request.url)
              val default = super.shouldInterceptRequest(view, request)
              val newRequest = override ?: default
              Timber.i("shouldInterceptRequest:\t${request.url}\t${override?.statusCode}")
              return newRequest
            }

            override fun onReceivedHttpError(
              view: WebView,
              request: WebResourceRequest,
              errorResponse: WebResourceResponse,
            ) {
              Timber.e("onReceivedHttpError:\t${request.url}\t${errorResponse.statusCode}")
            }
          }

        webChromeClient =
          object : WebChromeClient() {
            override fun onProgressChanged(
              view: WebView?,
              newProgress: Int,
            ) {
              super.onProgressChanged(view, newProgress)

              Timber.i("onProgressChanged:\t$newProgress")
            }
          }

        if (runningInPreview) {
          return@apply
        }

        settings.apply {
          allowFileAccess = false
          allowContentAccess = false
          setGeolocationEnabled(false)

          // Handle dark mode for WebView
          if (isFeatureSupported(ALGORITHMIC_DARKENING)) {
            setAlgorithmicDarkeningAllowed(this, true)
          } else if (isFeatureSupported(FORCE_DARK)) {
            when (resources.configuration.uiMode and UI_MODE_NIGHT_MASK) {
              UI_MODE_NIGHT_YES -> setForceDark(this, FORCE_DARK_ON)
              else -> setForceDark(this, FORCE_DARK_OFF)
            }
          } else {
            Timber.w("Dark mode not set")
          }
        }
      }
    },
    update = { webView ->
      // from "main/assets/index.html" -> "file:///android_asset/index.html"
      webView.loadUrl("https://appassets.androidplatform.net/assets/open_source_licenses.html")
    },
  )
}
