package burrows.apps.example.gif.presentation.main

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
import burrows.apps.example.gif.data.model.RiffsyResponseDto
import burrows.apps.example.gif.data.repository.ImageApiRepository
import burrows.apps.example.gif.data.repository.RiffsyApiClient
import burrows.apps.example.gif.SchedulerProvider
import burrows.apps.example.gif.presentation.adapter.GifAdapter
import burrows.apps.example.gif.presentation.adapter.GifItemDecoration
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.squareup.leakcanary.RefWatcher
import dagger.android.AndroidInjection
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_preview.view.*

/**
 * Main activity that will load our Fragments via the Support Fragment Manager.
 */
class MainActivity : DaggerAppCompatActivity(), MainContract.View, GifAdapter.OnItemClickListener {
  companion object {
    private const val TAG = "MainActivity"
    private const val PORTRAIT_COLUMNS = 3
    private const val VISIBLE_THRESHOLD = 5
  }
  private lateinit var layoutManager: GridLayoutManager
  private lateinit var itemOffsetDecoration: GifItemDecoration
  private lateinit var adapter: GifAdapter
  private lateinit var dialog: AppCompatDialog
  private lateinit var dialogText: TextView
  private lateinit var progressBar: ProgressBar
  private lateinit var imageView: ImageView
  private lateinit var presenter: MainContract.Presenter
  private var hasSearched = false
  private var previousTotal = 0
  private var loading = true
  private var firstVisibleItem = 0
  private var visibleItemCount = 0
  private var totalItemCount = 0
  private var next: Double? = null
  @Inject lateinit var refWatcher: RefWatcher
  @Inject lateinit var repository: ImageApiRepository
  @Inject lateinit var client: RiffsyApiClient
  @Inject lateinit var schedulerProvider: SchedulerProvider

  //
  // Contract
  //

  override fun setPresenter(presenter: MainContract.Presenter) {
    this.presenter = presenter
  }

  override fun clearImages() {
    // Clear current data
    adapter.clear()
  }

  override fun addImages(riffsyResponseDto: RiffsyResponseDto) {
    next = riffsyResponseDto.next

    riffsyResponseDto.results?.forEach {
      val url = it.media?.first()?.gif?.url

      adapter.add(ImageInfoModel(url = url))

      if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "ORIGINAL_IMAGE_URL\t $url")
    }
  }

  override fun showDialog(imageInfoModel: ImageInfoModel) {
    showImageDialog(imageInfoModel)
  }

  override fun isActive(): Boolean = !isFinishing

  //
  // GifAdapter
  //

  override fun onClick(imageInfoModel: ImageInfoModel) {
    showDialog(imageInfoModel)
  }

  //
  // Activity
  //

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Injection dependencies
    AndroidInjection.inject(this)

    MainPresenter(this, client, schedulerProvider)

    // Setup
    toolbar.setTitle(R.string.main_screen_title)
    setSupportActionBar(toolbar)

    layoutManager = GridLayoutManager(this, PORTRAIT_COLUMNS)
    itemOffsetDecoration = GifItemDecoration(
      this.resources.getDimensionPixelSize(R.dimen.gif_adapter_item_offset),
      layoutManager.spanCount)
    adapter = GifAdapter(this, repository)
    adapter.setHasStableIds(true)

    // Setup RecyclerView
    recyclerView.layoutManager = layoutManager
    recyclerView.addItemDecoration(itemOffsetDecoration)
    recyclerView.adapter = adapter
    recyclerView.setHasFixedSize(true)
    recyclerView.setItemViewCacheSize(RiffsyApiClient.DEFAULT_LIMIT_COUNT)
    recyclerView.isDrawingCacheEnabled = true
    recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    recyclerView.recycledViewPool.setMaxRecycledViews(0, PORTRAIT_COLUMNS * 2) // default 5
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
    })

    // Custom view for Dialog
    val dialogView = View.inflate(this, R.layout.dialog_preview, null)

    // Customize Dialog
    dialog = AppCompatDialog(this)
    dialog.setContentView(dialogView)
    dialog.setCancelable(true)
    dialog.setCanceledOnTouchOutside(true)
    dialog.setOnDismissListener {
      // https://github.com/bumptech/glide/issues/624#issuecomment-140134792
      Glide.with(imageView.context).clear(imageView)  // Forget view, try to free resources
      imageView.setImageDrawable(null)
      progressBar.visibility = View.VISIBLE // Make sure to show progress when loading new view
    }

    // Dialog views
    dialogText = dialogView.gifDialogTitle
    progressBar = dialogView.gifDialogProgress
    imageView = dialogView.gifDialogImage

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
    menuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
      override fun onMenuItemActionExpand(item: MenuItem): Boolean = true

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
    })

    // Query listener for search bar
    searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

      override fun onQueryTextSubmit(query: String): Boolean = false
    })

    return true
  }

  override fun onResume() {
    super.onResume()

    presenter.subscribe()
  }

  override fun onPause() {
    super.onPause()

    presenter.unsubscribe()
  }

  override fun onDestroy() {
    super.onDestroy()

    refWatcher.watch(this, TAG)
  }

  //
  // Private
  //

  private fun showImageDialog(imageInfoModel: ImageInfoModel) {
    dialog.show()
    // Remove "white" background for dialog
    dialog.window.decorView.setBackgroundResource(android.R.color.transparent)

    // Load associated text
    dialogText.text = imageInfoModel.url
    dialogText.visibility = View.VISIBLE

    // Load image
    repository.load(imageInfoModel.url)
      .thumbnail(repository.load(imageInfoModel.previewUrl))
      .listener(imageRequestListener)
      .into(imageView)
  }

  private val imageRequestListener = object : RequestListener<GifDrawable> {
    override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?,
                                 dataSource: DataSource?, isFirstResource: Boolean): Boolean {
      // Hide progressbar
      progressBar.visibility = View.GONE
      if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "finished loading\t $model")

      return false
    }

    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?,
                              isFirstResource: Boolean): Boolean {
      // Hide progressbar
      progressBar.visibility = View.GONE
      if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "finished loading\t $model")

      return false
    }
  }
}
