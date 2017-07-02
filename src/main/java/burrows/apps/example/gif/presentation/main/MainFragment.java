package burrows.apps.example.gif.presentation.main;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import burrows.apps.example.gif.App;
import burrows.apps.example.gif.R;
import burrows.apps.example.gif.data.rest.model.Result;
import burrows.apps.example.gif.data.rest.model.RiffsyResponse;
import burrows.apps.example.gif.data.rest.repository.ImageApiRepository;
import burrows.apps.example.gif.data.rest.repository.RiffsyApiClient;
import burrows.apps.example.gif.databinding.DialogPreviewBinding;
import burrows.apps.example.gif.databinding.FragmentMainBinding;
import burrows.apps.example.gif.presentation.adapter.GifAdapter;
import burrows.apps.example.gif.presentation.adapter.GifItemDecoration;
import burrows.apps.example.gif.presentation.adapter.model.ImageInfoModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Inject;

/**
 * Main Fragment of the application that displays the Recylcerview of Gif images.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class MainFragment extends Fragment
  implements IMainView, GifAdapter.OnItemClickListener {
  private static final String TAG = MainFragment.class.getSimpleName();
  // Can't be longer than 23 chars
  private static final int PORTRAIT_COLUMNS = 3;
  private GridLayoutManager layoutManager;
  private GifItemDecoration itemOffsetDecoration;
  private GifAdapter adapter;
  private boolean hasSearched;
  private AppCompatDialog dialog;
  private int previousTotal = 0;
  private boolean loading = true;
  private int visibleThreshold = 5;
  private int firstVisibleItem;
  private int visibleItemCount;
  private int totalItemCount;
  private Float next;
  private TextView dialogText;
  private ProgressBar progressBar;
  private ImageView imageView;
  private IMainPresenter presenter;
  @Inject RefWatcher refWatcher;
  @Inject ImageApiRepository repository;

  //
  // Contract
  //

  @Override public void setPresenter(IMainPresenter presenter) {
    this.presenter = presenter;
  }

  @Override public void clearImages() {
    // Clear current data
    adapter.clear();
  }

  @Override public void addImages(RiffsyResponse response) {
    next = response.getPage();
    // Iterate over data from response and grab the urls
    for (Result result : response.getResults()) {
      final String url = result.getMedia().get(0).getGif().getUrl();

      adapter.add(new ImageInfoModel(url, null));

      if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "ORIGINAL_IMAGE_URL\t" + url);
    }
  }

  @Override public void showDialog(ImageInfoModel imageInfo) {
    showImageDialog(imageInfo);
  }

  @Override public boolean isActive() {
    return isAdded();
  }

  //
  // GifAdapter
  //

  @Override public void onClick(ImageInfoModel imageInfo) {
    showDialog(imageInfo);
  }

  //
  // Fragment
  //

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Injection dependencies
    ((App) getActivity().getApplication()).getActivityComponent().inject(this);

    setHasOptionsMenu(true);

    layoutManager = new GridLayoutManager(getActivity(), PORTRAIT_COLUMNS);
    itemOffsetDecoration = new GifItemDecoration(
      getActivity().getResources().getDimensionPixelSize(R.dimen.gif_adapter_item_offset),
      layoutManager.getSpanCount());
    adapter = new GifAdapter(this, repository);
    adapter.setHasStableIds(true);
  }

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    final FragmentMainBinding binding =
      DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

    // Setup RecyclerView
    binding.recyclerView.setLayoutManager(layoutManager);
    binding.recyclerView.addItemDecoration(itemOffsetDecoration);
    binding.recyclerView.setAdapter(adapter);
    binding.recyclerView.setHasFixedSize(true);
    // http://stackoverflow.com/questions/30511890/does-glide-queue-up-every-image-request-recyclerview-loads-are-very-slow-when-s#comment49135977_30511890
    binding.recyclerView.getRecycledViewPool()
      .setMaxRecycledViews(0, PORTRAIT_COLUMNS * 2); // default 5
    binding.recyclerView.setItemViewCacheSize(RiffsyApiClient.DEFAULT_LIMIT_COUNT);
    binding.recyclerView.setDrawingCacheEnabled(true);
    binding.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    binding.recyclerView.addOnScrollListener(new OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        // Continuous scrolling
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (loading && (totalItemCount > previousTotal)) {
          loading = false;
          previousTotal = totalItemCount;
        }

        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem
          + visibleThreshold)) {
          presenter.loadTrendingImages(next);

          loading = true;
        }
      }
    });

    // Custom view for Dialog
    final DialogPreviewBinding previewBinding =
      DataBindingUtil.inflate(inflater, R.layout.dialog_preview, null, false);

    // Customize Dialog
    dialog = new AppCompatDialog(getContext());
    dialog.setContentView(previewBinding.getRoot());
    dialog.setCancelable(true);
    dialog.setCanceledOnTouchOutside(true);
    dialog.setOnDismissListener(new OnDismissListener() {
      @Override public void onDismiss(DialogInterface dialog1) {
        // https://github.com/bumptech/glide/issues/624#issuecomment-140134792
        Glide.clear(imageView);  // Forget view, try to free resources
        imageView.setImageDrawable(null);
        progressBar.setVisibility(View.VISIBLE); // Make sure to show progress when loading new view
      }
    });

    // Dialog views
    dialogText = previewBinding.gifDialogTitle;
    progressBar = previewBinding.gifDialogProgress;
    imageView = previewBinding.gifDialogImage;

    // Load initial images
    presenter.loadTrendingImages(next);

    return binding.getRoot();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.menu_fragment_main, menu);

    final MenuItem menuItem = menu.findItem(R.id.menu_search);

    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
    searchView.setQueryHint(searchView.getContext().getString(R.string.search_gifs));

    // Set contextual action on search icon click
    MenuItemCompat.setOnActionExpandListener(menuItem, new OnActionExpandListener() {
      @Override public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
      }

      @Override public boolean onMenuItemActionCollapse(MenuItem item) {
        // When search is closed, go back to trending getResults
        if (hasSearched) {
          // Reset
          presenter.clearImages();
          presenter.loadTrendingImages(next);
          hasSearched = false;
        }
        return true;
      }
    });

    // Query listener for search bar
    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      @Override public boolean onQueryTextChange(String newText) {
        // Search on type
        if (!TextUtils.isEmpty(newText)) {
          // Reset
          presenter.clearImages();
          presenter.loadSearchImages(newText, next);
          hasSearched = true;
        }
        return false;
      }

      @Override public boolean onQueryTextSubmit(String query) {
        return false;
      }
    });
  }

  @Override public void onResume() {
    super.onResume();

    presenter.subscribe();
  }

  @Override public void onPause() {
    super.onPause();

    presenter.unsubscribe();
  }

  @Override public void onDestroy() {
    super.onDestroy();

    refWatcher.watch(this, TAG);
  }

  private void showImageDialog(ImageInfoModel imageInfo) {
    dialog.show();
    // Remove "white" background for dialog
    dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

    // Load associated text
    dialogText.setText(imageInfo.getUrl());
    dialogText.setVisibility(View.VISIBLE);

    // Load image
    repository.load(imageInfo.getUrl())
      .thumbnail(repository.load(imageInfo.getPreviewUrl()))
      .listener(new RequestListener<Object, GifDrawable>() {
        @Override public boolean onException(Exception e, Object model, Target<GifDrawable> target,
          boolean isFirstResource) {
          // Hide progressbar
          progressBar.setVisibility(View.GONE);
          if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "finished loading\t" + model);

          return false;
        }

        @Override public boolean onResourceReady(GifDrawable resource, Object model,
          Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
          // Hide progressbar
          progressBar.setVisibility(View.GONE);
          if (Log.isLoggable(TAG, Log.INFO)) Log.i(TAG, "finished loading\t" + model);

          return false;
        }
      })
      .into(imageView);
  }
}
