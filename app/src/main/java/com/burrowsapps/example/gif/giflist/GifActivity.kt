package com.burrowsapps.example.gif.giflist

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.burrowsapps.example.gif.R
import com.burrowsapps.example.gif.data.ImageService
import com.burrowsapps.example.gif.data.RiffsyApiClient
import com.burrowsapps.example.gif.data.model.RiffsyResponseDto
import com.burrowsapps.example.gif.databinding.ActivityMainBinding
import com.burrowsapps.example.gif.databinding.DialogPreviewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main activity that will load our Fragments via the Support Fragment Manager.
 */
@AndroidEntryPoint
class GifActivity : AppCompatActivity(), GifContract.View, GifAdapter.OnItemClickListener {
  @Inject lateinit var gifPresenter: GifPresenter
  @Inject lateinit var imageService: ImageService
  private lateinit var binding: ActivityMainBinding
  private lateinit var dialogBinding: DialogPreviewBinding
  internal lateinit var gridLayoutManager: GridLayoutManager
  private lateinit var gifItemDecoration: GifItemDecoration
  private lateinit var gifAdapter: GifAdapter
  private lateinit var gifDialog: AppCompatDialog
  private lateinit var gifDialogText: TextView
  internal lateinit var gifDialogProgressBar: ProgressBar
  private lateinit var gifImageView: ImageView
  internal var hasSearchedImages = false
  internal var previousImageCount = 0
  internal var loadingImages = true
  internal var firstVisibleImage = 0
  internal var visibleImageCount = 0
  internal var totalImageCount = 0
  internal var nextPageNumber: Double? = null

  //
  // Activity
  //

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
    dialogBinding = DialogPreviewBinding.inflate(layoutInflater)

    // Setup
    binding.toolbar.setTitle(R.string.main_screen_title)
    setSupportActionBar(binding.toolbar)

    gridLayoutManager = GridLayoutManager(this, PORTRAIT_COLUMNS)
    gifItemDecoration = GifItemDecoration(
      resources.getDimensionPixelSize(R.dimen.gif_adapter_item_offset),
      gridLayoutManager.spanCount
    )
    gifAdapter = GifAdapter(this, imageService).apply {
      setHasStableIds(true)
    }

    // Setup RecyclerView
    binding.recyclerView.apply {
      layoutManager = gridLayoutManager
      addItemDecoration(gifItemDecoration)
      adapter = gifAdapter
      setHasFixedSize(true)
      setItemViewCacheSize(RiffsyApiClient.DEFAULT_LIMIT_COUNT)
      recycledViewPool.setMaxRecycledViews(0, PORTRAIT_COLUMNS * 2) // default 5
      addOnScrollListener(
        object : RecyclerView.OnScrollListener() {
          override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            // Continuous scrolling
            visibleImageCount = recyclerView.childCount
            totalImageCount = gridLayoutManager.itemCount
            firstVisibleImage = gridLayoutManager.findFirstVisibleItemPosition()

            if (loadingImages && totalImageCount > previousImageCount) {
              loadingImages = false
              previousImageCount = totalImageCount
            }

            if (!loadingImages &&
              totalImageCount - visibleImageCount <= firstVisibleImage + VISIBLE_THRESHOLD
            ) {
              gifPresenter.loadTrendingImages(nextPageNumber)

              loadingImages = true
            }
          }
        }
      )
    }

    // Custom view for Dialog
    val dialogView = View.inflate(this, R.layout.dialog_preview, null)

    // Customize Dialog
    gifDialog = AppCompatDialog(this).apply {
      setContentView(dialogView)
      setCancelable(true)
      setCanceledOnTouchOutside(true)
      setOnDismissListener {
        // https://github.com/bumptech/glide/issues/624#issuecomment-140134792
        Glide.with(gifImageView.context).clear(gifImageView) // Forget view, try to free resources
        gifImageView.setImageDrawable(null)
        gifDialogProgressBar.visibility = View.VISIBLE // Make sure to show progress when loadingImages new view
      }
    }

    // Dialog views
    gifDialogText = dialogBinding.gifDialogTitle
    gifDialogProgressBar = dialogBinding.gifDialogProgress
    gifImageView = dialogBinding.gifDialogImage

    // Load initial images
    gifPresenter.loadTrendingImages(nextPageNumber)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    super.onCreateOptionsMenu(menu)
    menuInflater.inflate(R.menu.menu_fragment_main, menu)

    menu.findItem(R.id.menu_search).apply {
      // Set contextual action on search icon click
      setOnActionExpandListener(
        object : MenuItem.OnActionExpandListener {
          override fun onMenuItemActionExpand(item: MenuItem) = true

          override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
            // When search is closed, go back to trending getResults
            if (hasSearchedImages) {
              // Reset
              gifPresenter.apply {
                clearImages()
                loadTrendingImages(nextPageNumber)
              }
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
                gifPresenter.apply {
                  clearImages()
                  loadSearchImages(newText, nextPageNumber)
                }
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

  override fun onResume() {
    super.onResume()

    gifPresenter.takeView(this)
  }

  override fun onPause() {
    gifPresenter.dropView()

    super.onPause()
  }

  //
  // MainContract.View
  //

  override fun clearImages() {
    // Clear current data
    gifAdapter.clear()
  }

  override fun addImages(riffsyResponseDto: RiffsyResponseDto) {
    nextPageNumber = riffsyResponseDto.next

    riffsyResponseDto.results.forEach { result ->
      val gif = result.media.first().gif
      val gifUrl = gif.url
      val gifPreviewUrl = gif.preview

      gifAdapter.add(GifImageInfo(gifUrl, gifPreviewUrl))

      if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "ORIGINAL_IMAGE_URL\t $gifUrl")
    }
  }

  override fun showDialog(imageInfoModel: GifImageInfo) {
    showImageDialog(imageInfoModel)
  }

  override fun isActive() = !isFinishing

  //
  // GifAdapter
  //

  override fun onClick(imageInfoModel: GifImageInfo) {
    showDialog(imageInfoModel)
  }

  //
  // Private
  //

  private fun showImageDialog(imageInfoModel: GifImageInfo) {
    gifDialog.apply {
      show()
      // Remove "white" background for gifDialog
      window?.decorView?.setBackgroundResource(android.R.color.transparent)
    }

    // Load associated text
    gifDialogText.apply {
      text = imageInfoModel.url
      visibility = View.VISIBLE
    }

    // Load image
    imageService.load(imageInfoModel.url)
      .thumbnail(imageService.load(imageInfoModel.previewUrl))
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
            gifDialogProgressBar.visibility = View.GONE
            if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "finished loadingImages\t $model")

            return false
          }

          override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<GifDrawable>?,
            isFirstResource: Boolean
          ): Boolean {
            // Hide progressbar
            gifDialogProgressBar.visibility = View.GONE
            if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "finished loadingImages\t $model")

            return false
          }
        }
      ).into(gifImageView)
  }

  companion object {
    private const val TAG = "MainActivity"
    private const val PORTRAIT_COLUMNS = 3
    private const val VISIBLE_THRESHOLD = 5
  }
}
