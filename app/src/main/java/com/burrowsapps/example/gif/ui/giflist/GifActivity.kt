package com.burrowsapps.example.gif.ui.giflist

import android.content.ClipData
import android.content.ClipboardManager
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.burrowsapps.example.gif.R
import com.burrowsapps.example.gif.data.ImageService
import com.burrowsapps.example.gif.data.source.network.TenorService.Companion.DEFAULT_LIMIT_COUNT
import com.burrowsapps.example.gif.databinding.ActivityGifBinding
import com.burrowsapps.example.gif.databinding.DialogPreviewBinding
import com.burrowsapps.example.gif.di.GlideApp
import com.burrowsapps.example.gif.ui.license.LicenseActivity
import com.burrowsapps.example.gif.ui.theme.GifTheme
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Main activity that will load our Fragments via the Support Fragment Manager.
 */
@AndroidEntryPoint
class GifActivity : AppCompatActivity() {
  @Inject internal lateinit var imageService: ImageService
  @Inject internal lateinit var clipboardManager: ClipboardManager
  internal val gifViewModel by viewModels<GifViewModel>()
  private lateinit var activityBinding: ActivityGifBinding
  private lateinit var dialogBinding: DialogPreviewBinding
  private lateinit var gridLayoutManager: GridLayoutManager
  private lateinit var gifItemDecoration: GifItemDecoration
  private lateinit var gifAdapter: GifAdapter
  private lateinit var gifDialog: AppCompatDialog
  private var hasSearchedImages = false
  private var loadingImages = true
  private var firstVisibleImage = 0
  private var visibleImageCount = 0
  private var totalImageCount = 0
  private var nextPageNumber: String? = null
  private var searchedImageText: String = ""

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    activityBinding = ActivityGifBinding.inflate(layoutInflater)
    setContentView(activityBinding.root)
    dialogBinding = DialogPreviewBinding.inflate(layoutInflater)

    val greeting = findViewById<ComposeView>(R.id.greeting)
    greeting.setContent {
      GifTheme {

      }
    }

    // Setup
    activityBinding.toolbar.setTitle(R.string.main_screen_title)
    setSupportActionBar(activityBinding.toolbar)

    gridLayoutManager = GridLayoutManager(this, PORTRAIT_COLUMNS)
    gifItemDecoration = GifItemDecoration(
      (1.0 * Resources.getSystem().displayMetrics.density).roundToInt(), // TODO 1.dp in compose
      gridLayoutManager.spanCount
    )
    gifAdapter = GifAdapter(imageService) { imageInfoModel ->
      showImageDialog(imageInfoModel)
    }

    // Setup RecyclerView
    activityBinding.recyclerView.apply {
      layoutManager = gridLayoutManager
      addItemDecoration(gifItemDecoration)
      adapter = gifAdapter
      setHasFixedSize(true)
      setItemViewCacheSize(DEFAULT_LIMIT_COUNT) // default 2
      recycledViewPool.setMaxRecycledViews(0, PORTRAIT_COLUMNS * 2) // default 5
      addOnScrollListener(
        object : RecyclerView.OnScrollListener() {
          override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            // Continuous scrolling
            visibleImageCount = recyclerView.childCount
            totalImageCount = gridLayoutManager.itemCount
            firstVisibleImage = gridLayoutManager.findFirstVisibleItemPosition()

            if (loadingImages) {
              if ((visibleImageCount + firstVisibleImage) >= totalImageCount) {
                loadImages()
              }
            }
          }
        }
      )
    }

    activityBinding.swipeRefresh.setOnRefreshListener {
      loadImages()
    }

    // Custom view for Dialog
    gifDialog = AppCompatDialog(this).apply {
      setContentView(dialogBinding.root)
      setCancelable(true)
      setCanceledOnTouchOutside(true)
      setOnDismissListener {
//        dialogBinding.gifDialogProgress.show() // Make sure to show progress when loadingImages new view
      }

      // Remove "white" background for gifDialog
      window?.decorView?.setBackgroundResource(android.R.color.transparent)
    }

    // Load initial images
    gifViewModel.apply {
      loadTrendingImages()

      trendingResponse.observe(this@GifActivity) { response ->
        updateList(response)
      }

      searchResponse.observe(this@GifActivity) { response ->
        updateList(response)
      }

      nextPageResponse.observe(this@GifActivity) { response ->
        nextPageNumber = response
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    super.onCreateOptionsMenu(menu)

    // Search for Gifs
    menu.add(0, MENU_SEARCH, 0, R.string.menu_search).apply {
      setIcon(R.drawable.ic_search_white_24dp)
      setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM or MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
      actionView = SearchView(this@GifActivity)
      apply {
        // Set contextual action on search icon click
        setOnActionExpandListener(
          object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem) = true

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
              // When search is closed, go back to trending getResults
              if (hasSearchedImages) {
                // Reset
                clearImages()
                nextPageNumber = null // reset pagination
                gifViewModel.loadTrendingImages()

                hasSearchedImages = false
              }
              return true
            }
          }
        )

        (actionView as SearchView).apply {
          queryHint = context.getString(R.string.search_gifs)
          // Query listener for search bar
          setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
              override fun onQueryTextChange(newText: String): Boolean {
                // Search on type
                if (newText.isNotEmpty()) {
                  // Reset
                  clearImages()
                  searchedImageText = newText
                  nextPageNumber = null // reset pagination
                  gifViewModel.loadSearchImages(searchedImageText)

                  hasSearchedImages = true
                }
                return false
              }

              override fun onQueryTextSubmit(query: String) = false
            }
          )
        }
      }
    }

    // License Activity
    menu.add(0, MENU_LICENSE, 1, R.string.menu_licenses)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      MENU_LICENSE -> {
        startActivity(LicenseActivity.createIntent(this))
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  internal fun loadImages() {
    loadingImages = false

    activityBinding.swipeRefresh.isRefreshing = true

    if (hasSearchedImages) {
      gifViewModel.loadSearchImages(searchedImageText, nextPageNumber)
      Timber.i("onScrolled:\tloadSearchImages")
    } else {
      gifViewModel.loadTrendingImages(nextPageNumber)
      Timber.i("onScrolled:\tloadTrendingImages")
    }

    loadingImages = true
  }

  internal fun clearImages() {
    // Clear current data
    gifAdapter.clear()
  }

  private fun updateList(newList: List<GifImageInfo>?) {
    if (newList == null) {
      Snackbar.make(activityBinding.root, getString(R.string.error_loading_list), LENGTH_SHORT)
        .show()
    } else {
      gifAdapter.add(newList)
    }
    activityBinding.swipeRefresh.isRefreshing = false
  }

  private fun showImageDialog(imageInfoModel: GifImageInfo) {
    // Load associated text
//    dialogBinding.gifDialogTitle.apply {
//      text = imageInfoModel.gifUrl
//      setOnClickListener {
//        clipboardManager.setPrimaryClip(
//          ClipData.newPlainText(CLIP_DATA_IMAGE_URL, imageInfoModel.gifUrl)
//        )
//        Snackbar.make(activityBinding.root, getString(R.string.copied_to_clipboard), LENGTH_SHORT)
//          .show()
//      }
//    }
//
//    // Load image - click on 'tinyGifPreviewUrl' -> 'tinyGifUrl' -> 'gifUrl'
//    imageService.loadGif(
//      imageUrl = imageInfoModel.tinyGifUrl,
//      thumbnailUrl = imageInfoModel.tinyGifPreviewUrl,
//      imageView = dialogBinding.gifDialogImage,
//      onResourceReady = {
//        // Hide progressbar
//        dialogBinding.gifDialogProgress.hide()
//        dialogBinding.gifDialogTitle.visibility = View.VISIBLE
//
//        Timber.i("onResourceReady")
//      },
//      onLoadFailed = { e ->
//        // Hide progressbar
//        dialogBinding.gifDialogProgress.hide()
//
//        Timber.e(e, "onLoadFailed")
//      },
//    )
//
//    gifDialog.show()
  }

  private companion object {
    private const val CLIP_DATA_IMAGE_URL = "https-image-url"
    private const val PORTRAIT_COLUMNS = 3
    private const val MENU_SEARCH = 0
    private const val MENU_LICENSE = 1
  }
}
