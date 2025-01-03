package com.burrowsapps.gif.search

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL_7_PRO
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.burrowsapps.gif.search.ui.giflist.GifScreen
import com.burrowsapps.gif.search.ui.license.LicenseScreen
import com.burrowsapps.gif.search.ui.theme.GifTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The main activity for this app.
 *
 * This class is annotated with @AndroidEntryPoint, which enables the Hilt dependency injection
 * framework for this activity. It extends the ComponentActivity class and overrides its onCreate()
 * method to set the content view using Compose.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    setContent {
      GifTheme {
        MainScreen()
      }
    }
  }
}

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
    MainScreen(
      navController = navController,
    )
  }
}

/**
 * The main screen for this app.
 *
 * This composable function represents the main screen of the app. It uses the Jetpack Compose UI
 * toolkit to create a navigation graph with two destinations: GifScreen and LicenseScreen. The
 * function also sets up a scaffold with a top app bar and a bottom navigation bar, and uses the
 * rememberNavController function to manage the navigation between the two destinations.
 */
@Composable
fun MainScreen(
  modifier: Modifier = Modifier,
  navController: NavHostController = rememberNavController(),
) {
  Scaffold(
    modifier = modifier,
  ) { innerPadding ->
    NavHost(
      modifier = Modifier.padding(innerPadding),
      navController = navController,
      startDestination = Screen.Gif.route,
    ) {
      composable(
        route = Screen.Gif.route,
      ) {
        GifScreen(
          navController = navController,
        )
      }
      composable(
        route = Screen.License.route,
      ) {
        LicenseScreen(
          navController = navController,
        )
      }
    }
  }
}

/**
 * A sealed class that represents the different screens in the app.
 *
 * This sealed class has two subclasses: Gif and License. Each subclass represents a screen in the
 * app, and has a route property that corresponds to the screen's route in the navigation graph.
 */
sealed class Screen(val route: String) {
  data object Gif : Screen("gif")

  data object License : Screen("license")
}
