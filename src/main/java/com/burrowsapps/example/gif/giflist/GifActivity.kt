package com.burrowsapps.example.gif.giflist

import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.burrowsapps.example.gif.data.ImageService
import com.burrowsapps.example.gif.data.RiffsyApiClient
import com.burrowsapps.example.gif.data.model.RiffsyResponseDto
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.burrowsapps.example.gif.R
import com.squareup.leakcanary.RefWatcher
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.recyclerView
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.dialog_preview.view.gifDialogImage
import kotlinx.android.synthetic.main.dialog_preview.view.gifDialogProgress
import kotlinx.android.synthetic.main.dialog_preview.view.gifDialogTitle
import javax.inject.Inject

/**
 * Main activity that will load our Fragments via the Support Fragment Manager.
 */
class GifActivity : DaggerAppCompatActivity(), GifContract.View, GifAdapter.OnItemClickListener {
    companion object {
        private const val TAG = "MainActivity"
        private const val PORTRAIT_COLUMNS = 3
        private const val VISIBLE_THRESHOLD = 5
    }

    @Inject lateinit var gifPresenter: GifPresenter
    @Inject lateinit var refWatcher: RefWatcher
    @Inject lateinit var imageService: ImageService
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var gifItemDecoration: GifItemDecoration
    private lateinit var gifAdapter: GifAdapter
    private lateinit var gifDialog: AppCompatDialog
    private lateinit var gifDialogText: TextView
    private lateinit var gifDialogProgressBar: ProgressBar
    private lateinit var gifImageView: ImageView
    private var hasSearchedImages = false
    private var previousImageCount = 0
    private var loadingImages = true
    private var firstVisibleImage = 0
    private var visibleImageCount = 0
    private var totalImageCount = 0
    private var nextPageNumber: Double? = null
    private val imageRequestListener = object : RequestListener<GifDrawable> {
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
    private val searchOnQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(newText: String): Boolean {
            // Search on type
            if (!TextUtils.isEmpty(newText)) {
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
    private val searchOnActionExpandListener = object : MenuItem.OnActionExpandListener {
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
    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
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
                totalImageCount - visibleImageCount <= firstVisibleImage + VISIBLE_THRESHOLD) {
                gifPresenter.loadTrendingImages(nextPageNumber)

                loadingImages = true
            }
        }
    }

    //
    // Activity
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Injection dependencies
        AndroidInjection.inject(this)

        // Setup
        toolbar.setTitle(R.string.main_screen_title)
        setSupportActionBar(toolbar)

        gridLayoutManager = GridLayoutManager(this, PORTRAIT_COLUMNS)
        gifItemDecoration = GifItemDecoration(
            resources.getDimensionPixelSize(R.dimen.gif_adapter_item_offset),
            gridLayoutManager.spanCount)
        gifAdapter = GifAdapter(this, imageService).apply {
            setHasStableIds(true)
        }

        // Setup RecyclerView
        recyclerView.apply {
            layoutManager = gridLayoutManager
            addItemDecoration(gifItemDecoration)
            adapter = gifAdapter
            setHasFixedSize(true)
            setItemViewCacheSize(RiffsyApiClient.DEFAULT_LIMIT_COUNT)
            recycledViewPool.setMaxRecycledViews(0, PORTRAIT_COLUMNS * 2) // default 5
            addOnScrollListener(recyclerViewOnScrollListener)
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
        gifDialogText = dialogView.gifDialogTitle
        gifDialogProgressBar = dialogView.gifDialogProgress
        gifImageView = dialogView.gifDialogImage

        // Load initial images
        gifPresenter.loadTrendingImages(nextPageNumber)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_fragment_main, menu)

        menu.findItem(R.id.menu_search).apply {
            // Set contextual action on search icon click
            setOnActionExpandListener(searchOnActionExpandListener)

            (actionView as SearchView).apply {
                queryHint = context.getString(R.string.search_gifs)
                // Query listener for search bar
                setOnQueryTextListener(searchOnQueryTextListener)
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

    override fun onDestroy() {
        super.onDestroy()

        refWatcher.watch(this, TAG)
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
            .listener(imageRequestListener)
            .into(gifImageView)
    }
}
