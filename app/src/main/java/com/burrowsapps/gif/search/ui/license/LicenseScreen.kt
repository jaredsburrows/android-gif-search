@file:OptIn(ExperimentalMaterial3Api::class)

package com.burrowsapps.gif.search.ui.license

import android.content.Context
import android.content.res.Configuration
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.webkit.WebSettingsCompat.setAlgorithmicDarkeningAllowed
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewFeature.ALGORITHMIC_DARKENING
import androidx.webkit.WebViewFeature.isFeatureSupported
import com.burrowsapps.gif.search.R
import com.burrowsapps.gif.search.ui.theme.GifTheme
import timber.log.Timber

/** Shows the license screen of the app. */
@Preview(
  name = "dark",
  locale = "en",
  showSystemUi = true,
  showBackground = true,
  uiMode = Configuration.UI_MODE_NIGHT_YES,
  device = Devices.PIXEL_7_PRO,
)
@Preview(
  name = "light",
  locale = "en",
  showSystemUi = true,
  showBackground = true,
  uiMode = Configuration.UI_MODE_NIGHT_NO,
  device = Devices.PIXEL_7_PRO,
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
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
) {
  val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
  val context = LocalContext.current

  // https://developer.android.com/reference/androidx/webkit/WebViewAssetLoader
  // Instead of loading files using "files://" directly
  val assetLoader =
    remember(context) {
      WebViewAssetLoader
        .Builder()
        .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
        .build()
    }

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = { TheToolbar(navController, scrollBehavior) },
  ) { paddingValues ->
    TheContent(paddingValues, assetLoader)
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
        modifier =
          Modifier.semantics {
            contentDescription = context.getString(R.string.license_screen_content_description)
          },
        text = stringResource(R.string.license_screen_title),
      )
    },
    navigationIcon = {
      if (navController.previousBackStackEntry != null) {
        IconButton(onClick = { navController.navigateUp() }) {
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
private fun TheContent(
  innerPadding: PaddingValues,
  assetLoader: WebViewAssetLoader,
) {
  Column(
    modifier = Modifier.padding(innerPadding),
  ) {
    // TODO Nested WebView prevent anchor clicks
    TheWebView(assetLoader)
  }
}

@Composable
private fun TheWebView(assetLoader: WebViewAssetLoader) {
  val context = LocalContext.current
  val runningInPreview = LocalInspectionMode.current

  val webView =
    remember {
      configuredWebView(
        context = context,
        assetLoader = assetLoader,
        runningInPreview = runningInPreview,
      )
    }

  DisposableEffect(webView) {
    onDispose { webView.destroy() }
  }

  AndroidView(
    factory = { webView },
    modifier = Modifier.fillMaxSize(),
  ) {
    // from "main/assets/index.html" -> "file:///android_asset/index.html"
    it.loadUrl("https://appassets.androidplatform.net/assets/open_source_licenses.html")
  }
}

private fun configuredWebView(
  context: Context,
  assetLoader: WebViewAssetLoader,
  runningInPreview: Boolean,
): WebView =
  WebView(context).apply {
    isVerticalScrollBarEnabled = false

    webViewClient = LicenseWebViewClient(assetLoader)
    webChromeClient = LicenseWebChromeClient()

    if (!runningInPreview) {
      settings.apply {
        allowFileAccess = false
        allowContentAccess = false
        setGeolocationEnabled(false)
        cacheMode = WebSettings.LOAD_NO_CACHE
        builtInZoomControls = false
        displayZoomControls = false

        // Handle dark mode for WebView
        if (isFeatureSupported(ALGORITHMIC_DARKENING)) {
          setAlgorithmicDarkeningAllowed(this, true)
        } else {
          Timber.tag("LicenseScreen").w("Dark mode not set")
        }
      }
    }
  }
