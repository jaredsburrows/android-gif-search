package com.burrowsapps.gif.search.ui.license

import android.content.Context
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LicenseWebChromeClientTest {
  private lateinit var sut: LicenseWebChromeClient
  private lateinit var context: Context

  @Before
  fun setUp() {
    sut = LicenseWebChromeClient()
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun testOnProgressChanged() {
    val webView = WebView(context)
    val progress = 50

    sut.onProgressChanged(webView, progress)
  }
}
