@file:OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalMaterial3Api::class,
)

package com.burrowsapps.example.gif.ui.license

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
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
import com.burrowsapps.example.gif.R
import com.burrowsapps.example.gif.ui.theme.GifTheme
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.rememberWebViewState
import timber.log.Timber
import com.google.accompanist.web.WebView as AccompanistWebView

/** Shows the license screen of the app. */
@Preview(
  name = "dark",
  showBackground = true,
  device = Devices.PIXEL,
  locale = "en",
  showSystemUi = true,
  uiMode = UI_MODE_NIGHT_YES,
)
@Preview(
  name = "light",
  showBackground = true,
  device = Devices.PIXEL,
  locale = "en",
  showSystemUi = true,
  uiMode = UI_MODE_NIGHT_NO,
)
@Composable
fun DefaultPreview() {
  GifTheme {
    LicenseScreen()
  }
}

@Composable
fun LicenseScreen(navController: NavHostController = rememberNavController()) {
  val scrollBehavior = enterAlwaysScrollBehavior(rememberTopAppBarState())

  Scaffold(
    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = { TheToolbar(navController, scrollBehavior) }
  ) { paddingValues ->
    TheContent(paddingValues)
  }
}

@Composable
fun TheToolbar(
  navController: NavHostController,
  scrollBehavior: TopAppBarScrollBehavior,
) {
  TopAppBar(
    title = {
      Text(
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
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(R.string.menu_back),
          )
        }
      }
    },
    scrollBehavior = scrollBehavior
  )
}

@Composable
fun TheContent(innerPadding: PaddingValues) {
  Column(
    modifier = Modifier
      .padding(innerPadding)
      .verticalScroll(rememberScrollState()),
  ) {
    // TODO Nested WebView prevent anchor clicks
    TheWebView()
  }
}

@Composable
fun TheWebView() {
  val context = LocalContext.current
  // https://developer.android.com/reference/androidx/webkit/WebViewAssetLoader
  val state =
    rememberWebViewState("https://appassets.androidplatform.net/assets/open_source_licenses.html")
  val pathHandler = WebViewAssetLoader.AssetsPathHandler(context)
  // Instead of loading files using "files://" directly
  val assetLoader = WebViewAssetLoader.Builder().addPathHandler("/assets/", pathHandler).build()

  AccompanistWebView(
    state = state,
    onCreated = { webView ->
      webView.isVerticalScrollBarEnabled = false
      webView.settings.apply {
        allowFileAccess = false
        allowContentAccess = false
        setGeolocationEnabled(false)
        @Suppress(names = ["DEPRECATION"])
        if (SDK_INT < VERSION_CODES.R) {
          allowFileAccessFromFileURLs = false
          allowUniversalAccessFromFileURLs = false
        }

        // Handle dark mode for WebView
        @Suppress(names = ["DEPRECATION"])
        @SuppressLint("NewApi")
        if (SDK_INT < VERSION_CODES.Q && isFeatureSupported(ALGORITHMIC_DARKENING)) {
          setAlgorithmicDarkeningAllowed(this, true)
        } else if (isFeatureSupported(FORCE_DARK)) {
          when (webView.resources.configuration.uiMode and UI_MODE_NIGHT_MASK) {
            UI_MODE_NIGHT_YES -> setForceDark(this, FORCE_DARK_ON)
            else -> setForceDark(this, FORCE_DARK_OFF)
          }
        } else {
          Timber.w("Dark mode not set")
        }
      }
    },
    client = object : AccompanistWebViewClient() {
      override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest,
      ): WebResourceResponse? {
        // Override URLs for AssetsPathHandler
        return assetLoader.shouldInterceptRequest(request.url) ?: super.shouldInterceptRequest(
          view, request
        )
      }

      override fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest,
        errorResponse: WebResourceResponse,
      ) {
        Timber.e("onReceivedHttpError:\t${errorResponse.statusCode}\t${errorResponse.reasonPhrase}")
      }
    },
  )
}
