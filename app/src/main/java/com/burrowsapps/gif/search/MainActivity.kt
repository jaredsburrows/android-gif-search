@file:OptIn(
  ExperimentalMaterial3Api::class,
)

package com.burrowsapps.gif.search

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices.PIXEL
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.burrowsapps.gif.search.data.ImageService
import com.burrowsapps.gif.search.ui.giflist.GifScreen
import com.burrowsapps.gif.search.ui.license.LicenseScreen
import com.burrowsapps.gif.search.ui.theme.GifTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/** Single `Activity` that is the entry point of the app. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @Inject internal lateinit var imageService: ImageService

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      GifTheme {
        MainScreen(imageService)
      }
    }
  }
}

@Preview(
  name = "dark",
  showBackground = true,
  device = PIXEL,
  locale = "en",
  showSystemUi = true,
  uiMode = UI_MODE_NIGHT_YES,
)
@Preview(
  name = "light",
  showBackground = true,
  device = PIXEL,
  locale = "en",
  showSystemUi = true,
  uiMode = UI_MODE_NIGHT_NO,
)
@Composable
private fun DefaultPreview() {
  GifTheme {
    MainScreen()
  }
}

@Composable
fun MainScreen(imageService: ImageService = ImageService(LocalContext.current)) {
  val navController = rememberNavController()

  Scaffold { innerPadding ->
    NavHost(
      modifier = Modifier.padding(innerPadding),
      navController = navController,
      startDestination = Screen.HomeScreen.route,
    ) {
      composable(
        route = Screen.HomeScreen.route,
      ) {
        GifScreen(
          navController = navController,
          imageService = imageService,
        )
      }
      composable(
        route = Screen.LicenseScreen.route,
      ) {
        LicenseScreen(navController)
      }
    }
  }
}

sealed class Screen(val route: String) {
  object HomeScreen : Screen("home")
  object LicenseScreen : Screen("license")
}
