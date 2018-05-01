package burrows.apps.example.gif.giflist

import android.os.Bundle
import android.support.v7.app.AppCompatDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import burrows.apps.example.gif.R
import burrows.apps.example.gif.data.ImageService
import burrows.apps.example.gif.data.RiffsyApiClient
import burrows.apps.example.gif.data.model.RiffsyResponseDto
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
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

  @Inject lateinit var presenter: GifPresenter
  @Inject lateinit var refWatcher: RefWatcher
  @Inject lateinit var repository: ImageService
  private lateinit var layoutManager: GridLayoutManager
  private lateinit var gifItemDecoration: GifItemDecoration
  private lateinit var gifAdapter: GifAdapter
  private lateinit var gifDialog: AppCompatDialog
  private lateinit var gifDialogText: TextView
  private lateinit var gifDialogProgressBar: ProgressBar
  private lateinit var gifImageView: ImageView
  private var hasSearched = false
  private var previousTotal = 0
  private var loading = true
  private var firstVisibleItem = 0
  private var visibleItemCount = 0
  private var totalItemCount = 0
  private var next: Double? = null
  private val imageRequestListener = object : RequestListener<GifDrawable> {
    override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?,
                                 dataSource: DataSource?, isFirstResource: Boolean): Boolean {
      // Hide progressbar
      gifDialogProgressBar.visibility = View.GONE
      if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "finished loading\t $model")

      return false
    }

    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?,
                              isFirstResource: Boolean): Boolean {
      // Hide progressbar
      gifDialogProgressBar.visibility = View.GONE
      if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "finished loading\t $model")

      return false
    }
  }
  private val searchOnQueryTextListener = object : SearchView.OnQueryTextListener {
    override fun onQueryTextChange(newText: String): Boolean {
      // Search on type
      if (!TextUtils.isEmpty(newText)) {
        // Reset
        presenter.clearImages()
        presenter.loadSearchImages(newText, next)
        hasSearched = true
      }
      return false
    }

    override fun onQueryTextSubmit(query: String) = false
  }
  private val searchOnActionExpandListener = object : MenuItem.OnActionExpandListener {
    override fun onMenuItemActionExpand(item: MenuItem) = true

    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
      // When search is closed, go back to trending getResults
      if (hasSearched) {
        // Reset
        presenter.clearImages()
        presenter.loadTrendingImages(next)
        hasSearched = false
      }
      return true
    }
  }
  private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
      super.onScrolled(recyclerView, dx, dy)

      // Continuous scrolling
      visibleItemCount = recyclerView?.childCount ?: 0
      totalItemCount = layoutManager.itemCount
      firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

      if (loading && totalItemCount > previousTotal) {
        loading = false
        previousTotal = totalItemCount
      }

      if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + VISIBLE_THRESHOLD) {
        presenter.loadTrendingImages(next)

        loading = true
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

    layoutManager = GridLayoutManager(this, PORTRAIT_COLUMNS)
    gifItemDecoration = GifItemDecoration(
      resources.getDimensionPixelSize(R.dimen.gif_adapter_item_offset),
      layoutManager.spanCount)
    gifAdapter = GifAdapter(this, repository)
    gifAdapter.setHasStableIds(true)

    // Setup RecyclerView
    recyclerView.layoutManager = layoutManager
    recyclerView.addItemDecoration(gifItemDecoration)
    recyclerView.adapter = gifAdapter
    recyclerView.setHasFixedSize(true)
    recyclerView.setItemViewCacheSize(RiffsyApiClient.DEFAULT_LIMIT_COUNT)
    recyclerView.isDrawingCacheEnabled = true
    recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    recyclerView.recycledViewPool.setMaxRecycledViews(0, PORTRAIT_COLUMNS * 2) // default 5
    recyclerView.addOnScrollListener(recyclerViewOnScrollListener)

    // Custom view for Dialog
    val dialogView = View.inflate(this, R.layout.dialog_preview, null)

    // Customize Dialog
    gifDialog = AppCompatDialog(this)
    gifDialog.setContentView(dialogView)
    gifDialog.setCancelable(true)
    gifDialog.setCanceledOnTouchOutside(true)
    gifDialog.setOnDismissListener {
      // https://github.com/bumptech/glide/issues/624#issuecomment-140134792
      Glide.with(gifImageView.context).clear(gifImageView)  // Forget view, try to free resources
      gifImageView.setImageDrawable(null)
      gifDialogProgressBar.visibility = View.VISIBLE // Make sure to show progress when loading new view
    }

    // Dialog views
    gifDialogText = dialogView.gifDialogTitle
    gifDialogProgressBar = dialogView.gifDialogProgress
    gifImageView = dialogView.gifDialogImage

    // Load initial images
    presenter.loadTrendingImages(next)
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    super.onCreateOptionsMenu(menu)
    menuInflater.inflate(R.menu.menu_fragment_main, menu)

    val menuItem = menu.findItem(R.id.menu_search)
    val searchView = menuItem?.actionView as SearchView?
    searchView?.queryHint = searchView?.context?.getString(R.string.search_gifs)

    // Set contextual action on search icon click
    menuItem?.setOnActionExpandListener(searchOnActionExpandListener)

    // Query listener for search bar
    searchView?.setOnQueryTextListener(searchOnQueryTextListener)

    return true
  }

  override fun onResume() {
    super.onResume()

    presenter.takeView(this)
  }

  override fun onPause() {
    presenter.dropView()

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
    next = riffsyResponseDto.next

    riffsyResponseDto.results?.forEach {
      val first = it.media?.first()?.gif
      val url = first?.url
      val preview = first?.preview

      gifAdapter.add(GifImageInfo(url = url, previewUrl = preview))

      if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "ORIGINAL_IMAGE_URL\t $url")
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
    gifDialog.show()
    // Remove "white" background for gifDialog
    gifDialog.window.decorView.setBackgroundResource(android.R.color.transparent)

    // Load associated text
    gifDialogText.text = imageInfoModel.url
    gifDialogText.visibility = View.VISIBLE

    // Load image
    repository.load(imageInfoModel.url)
      .thumbnail(repository.load(imageInfoModel.previewUrl))
      .listener(imageRequestListener)
      .into(gifImageView)
  }
}
