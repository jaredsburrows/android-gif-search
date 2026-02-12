package com.burrowsapps.gif.search.ui.license

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.R
import com.burrowsapps.gif.search.ui.theme.GifTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LicenseScreenTest {
  @get:Rule
  internal val composeTestRule = createComposeRule()

  private val context = ApplicationProvider.getApplicationContext<Context>()
  private val licenseScreenTitle = context.getString(R.string.license_screen_title)
  private val licenseScreenContentDescription =
    context.getString(R.string.license_screen_content_description)
  private val menuBackContentDescription = context.getString(R.string.menu_back_content_description)

  @Test
  fun `license screen title is displayed`() {
    composeTestRule.setContent {
      CompositionLocalProvider(LocalInspectionMode provides true) {
        GifTheme {
          LicenseScreen()
        }
      }
    }

    composeTestRule.onNodeWithText(licenseScreenTitle).assertIsDisplayed()
  }

  @Test
  fun `license screen content description is displayed`() {
    composeTestRule.setContent {
      CompositionLocalProvider(LocalInspectionMode provides true) {
        GifTheme {
          LicenseScreen()
        }
      }
    }

    composeTestRule.onNodeWithContentDescription(licenseScreenContentDescription)
      .assertIsDisplayed()
  }

  @Test
  fun `back button is displayed when back stack entry exists`() {
    composeTestRule.setContent {
      CompositionLocalProvider(LocalInspectionMode provides true) {
        GifTheme {
          val navController = rememberNavController()
          NavHost(navController = navController, startDestination = "home") {
            composable("home") {}
            composable("license") {
              LicenseScreen(navController = navController)
            }
          }
          navController.navigate("license")
        }
      }
    }

    composeTestRule.onNodeWithContentDescription(menuBackContentDescription).assertIsDisplayed()
  }

  @Test
  fun `back button is not displayed when no back stack entry`() {
    composeTestRule.setContent {
      CompositionLocalProvider(LocalInspectionMode provides true) {
        GifTheme {
          LicenseScreen()
        }
      }
    }

    composeTestRule.onNodeWithContentDescription(menuBackContentDescription).assertDoesNotExist()
  }
}
