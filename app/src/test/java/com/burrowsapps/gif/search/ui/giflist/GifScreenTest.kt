package com.burrowsapps.gif.search.ui.giflist

import android.content.Context
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.burrowsapps.gif.search.R
import com.burrowsapps.gif.search.ui.theme.GifTheme
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifScreenTest {
  @get:Rule
  internal val composeTestRule = createComposeRule()

  private val context = ApplicationProvider.getApplicationContext<Context>()
  private val searchHint = context.getString(R.string.search_hint)
  private val menuSearchContentDescription =
    context.getString(R.string.menu_search_content_description)
  private val menuCloseContentDescription =
    context.getString(R.string.menu_close_content_description)
  private val menuMoreContentDescription = context.getString(R.string.menu_more_content_description)
  private val licenseScreenTitle = context.getString(R.string.license_screen_title)

  @Test
  fun `search icon is displayed`() {
    composeTestRule.setContent {
      GifTheme {
        SearchBar(
          query = "",
          onQueryChange = {},
          scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(),
          onFocusChange = {},
          onOpenLicenses = {},
        )
      }
    }

    composeTestRule.onNodeWithContentDescription(menuSearchContentDescription).assertIsDisplayed()
  }

  @Test
  fun `search hint placeholder is displayed`() {
    composeTestRule.setContent {
      GifTheme {
        SearchBar(
          query = "",
          onQueryChange = {},
          scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(),
          onFocusChange = {},
          onOpenLicenses = {},
        )
      }
    }

    composeTestRule.onNodeWithText(searchHint).assertIsDisplayed()
  }

  @Test
  fun `more button is displayed when query is empty`() {
    composeTestRule.setContent {
      GifTheme {
        SearchBar(
          query = "",
          onQueryChange = {},
          scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(),
          onFocusChange = {},
          onOpenLicenses = {},
        )
      }
    }

    composeTestRule.onNodeWithContentDescription(menuMoreContentDescription).assertIsDisplayed()
  }

  @Test
  fun `close button is displayed when query is not empty`() {
    composeTestRule.setContent {
      GifTheme {
        SearchBar(
          query = "hello",
          onQueryChange = {},
          scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(),
          onFocusChange = {},
          onOpenLicenses = {},
        )
      }
    }

    composeTestRule.onNodeWithContentDescription(menuCloseContentDescription).assertIsDisplayed()
  }

  @Test
  fun `close button clears query`() {
    var queryClearedToEmpty = false

    composeTestRule.setContent {
      GifTheme {
        SearchBar(
          query = "hello",
          onQueryChange = { if (it.isEmpty()) queryClearedToEmpty = true },
          scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(),
          onFocusChange = {},
          onOpenLicenses = {},
        )
      }
    }

    composeTestRule.onNodeWithContentDescription(menuCloseContentDescription).performClick()

    assertThat(queryClearedToEmpty).isTrue()
  }

  @Test
  fun `more button opens licenses menu item`() {
    composeTestRule.setContent {
      GifTheme {
        SearchBar(
          query = "",
          onQueryChange = {},
          scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior(),
          onFocusChange = {},
          onOpenLicenses = {},
        )
      }
    }

    composeTestRule.onNodeWithContentDescription(menuMoreContentDescription).performClick()

    composeTestRule.onNodeWithText(licenseScreenTitle).assertIsDisplayed()
  }
}
