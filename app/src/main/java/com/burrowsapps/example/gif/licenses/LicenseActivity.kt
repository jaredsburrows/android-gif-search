package com.burrowsapps.example.gif.licenses

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.burrowsapps.example.gif.R

/**
 * Open source license activity.
 */
class LicenseActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(WebView(this).apply {
      loadUrl("file:///android_asset/open_source_licenses.html")
    })

    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setHomeButtonEnabled(true)
      title = getString(R.string.menu_licenses)
    }
  }

  companion object {
    fun createIntent(context: Context): Intent {
      return Intent().setClass(context, LicenseActivity::class.java)
    }
  }
}
