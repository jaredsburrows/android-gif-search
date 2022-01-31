package com.burrowsapps.example.gif.ui.giflist

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.burrowsapps.example.gif.R
import com.burrowsapps.example.gif.data.ImageService
import com.burrowsapps.example.gif.data.source.network.TenorService.Companion.DEFAULT_LIMIT_COUNT
import com.burrowsapps.example.gif.databinding.ActivityGifBinding
import com.burrowsapps.example.gif.databinding.DialogPreviewBinding
import com.burrowsapps.example.gif.di.GlideApp
import com.burrowsapps.example.gif.ui.license.LicenseActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * Main activity that will load our Fragments via the Support Fragment Manager.
 */
@AndroidEntryPoint
class GifActivity : AppCompatActivity() {
  @Inject internal lateinit var imageService: ImageService
  @Inject internal lateinit var clipboardManager: ClipboardManager
  internal val gifViewModel by viewModels<GifViewModel>()
  private lateinit var binding: ActivityGifBinding
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
    binding = ActivityGifBinding.inflate(layoutInflater).also { setContentView(it.root) }
    dialogBinding = DialogPreviewBinding.inflate(layoutInflater)

    // Setup
    binding.toolbar.setTitle(R.string.main_screen_title)
    setSupportActionBar(binding.toolbar)

    gridLayoutManager = GridLayoutManager(this, PORTRAIT_COLUMNS)
    gifItemDecoration = GifItemDecoration(
      resources.getDimensionPixelSize(R.dimen.gif_adapter_item_offset),
      gridLayoutManager.spanCount
    )
    gifAdapter = GifAdapter(onItemClick = { imageInfoModel ->
      showImageDialog(imageInfoModel)
    }, imageService)

    // Setup RecyclerView
    binding.recyclerView.apply {
      layoutManager = gridLayoutManager
      addItemDecoration(gifItemDecoration)
      adapter = gifAdapter
      setHasFixedSize(true)
      setItemViewCacheSize(DEFAULT_LIMIT_COUNT) // default 2
      recycledViewPool.setMaxRecycledViews(0, PORTRAIT_COLUMNS * 2) // default 5
      addRecyclerListener { holder ->
        val gifViewHolder = holder as GifAdapter.ViewHolder
        GlideApp.with(this).clear(gifViewHolder.binding.gifImage)

        Timber.i("addRecyclerListener:\t${gifViewHolder.binding.gifImage}")
      }
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

    binding.swipeRefresh.setOnRefreshListener {
      loadImages()
    }

    // Custom view for Dialog
    gifDialog = AppCompatDialog(this).apply {
      setContentView(dialogBinding.root)
      setCancelable(true)
      setCanceledOnTouchOutside(true)
      setOnDismissListener {
        // https://github.com/bumptech/glide/issues/624#issuecomment-140134792
        GlideApp.with(dialogBinding.gifDialogImage.context)
          .clear(dialogBinding.gifDialogImage) // Forget view, try to free resources
        dialogBinding.gifDialogImage.setImageDrawable(null)
        dialogBinding.gifDialogProgress.show() // Make sure to show progress when loadingImages new view
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
    menuInflater.inflate(R.menu.menu_main, menu)

    menu.findItem(R.id.menuSearch).apply {
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
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menuLicenses -> {
        startActivity(LicenseActivity.createIntent(this))
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  internal fun loadImages() {
    loadingImages = false

    binding.swipeRefresh.isRefreshing = true

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
      Snackbar.make(binding.root, getString(R.string.error_loading_list), LENGTH_SHORT).show()
    } else {
      gifAdapter.add(newList)
    }
    binding.swipeRefresh.isRefreshing = false
  }

  private fun showImageDialog(imageInfoModel: GifImageInfo) {
    // Load associated text
    dialogBinding.gifDialogTitle.apply {
      text = imageInfoModel.gifUrl
      setOnClickListener {
        clipboardManager.setPrimaryClip(
          ClipData.newPlainText(CLIP_DATA_IMAGE_URL, imageInfoModel.gifUrl)
        )
        Snackbar.make(binding.root, getString(R.string.copied_to_clipboard), LENGTH_SHORT).show()
      }
    }

    // Load image - click on 'tinyGifPreviewUrl' -> 'tinyGifUrl' -> 'gifUrl'
    imageService.loadGif(imageInfoModel.gifUrl)
      .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
      .thumbnail(imageService.loadGif(imageInfoModel.tinyGifUrl))
      .listener(
        object : RequestListener<GifDrawable> {
          override fun onResourceReady(
            resource: GifDrawable?,
            model: Any?,
            target: Target<GifDrawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
          ): Boolean {
            // Hide progressbar
            dialogBinding.gifDialogProgress.hide()
            dialogBinding.gifDialogTitle.visibility = View.VISIBLE

            Timber.i("onResourceReady:\t$model")

            return false
          }

          override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<GifDrawable>?,
            isFirstResource: Boolean
          ): Boolean {
            // Hide progressbar
            dialogBinding.gifDialogProgress.hide()

            Timber.e(e, "onLoadFailed:\t$model")

            return false
          }
        }
      )
      .into(dialogBinding.gifDialogImage)
      .clearOnDetach()

    gifDialog.show()
  }

  companion object {
    private const val CLIP_DATA_IMAGE_URL = "https-image-url"
    private const val PORTRAIT_COLUMNS = 3
  }
}
