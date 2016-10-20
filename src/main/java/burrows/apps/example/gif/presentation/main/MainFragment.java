package burrows.apps.example.gif.presentation.main;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import burrows.apps.example.gif.App;
import burrows.apps.example.gif.R;
import burrows.apps.example.gif.data.rest.model.Result;
import burrows.apps.example.gif.data.rest.model.RiffsyResponse;
import burrows.apps.example.gif.data.rest.repository.ImageRepository;
import burrows.apps.example.gif.data.rest.repository.RiffsyRepository;
import burrows.apps.example.gif.ui.adapter.GifAdapter;
import burrows.apps.example.gif.ui.adapter.GifItemDecoration;
import burrows.apps.example.gif.ui.adapter.model.ImageInfo;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
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
public final class MainFragment extends Fragment implements MainContract.View, GifAdapter.OnItemClickListener {
  static final String TAG = MainFragment.class.getSimpleName();
  private static final int PORTRAIT_COLUMNS = 3;
  private GridLayoutManager layoutManager;
  private GifItemDecoration itemOffsetDecoration;
  private GifAdapter adapter;
  boolean hasSearched;
  private Dialog dialog;
  private Unbinder unbinder;
  AppCompatTextView dialogText;
  ProgressBar progressBar;
  ImageView imageView;
  MainContract.Presenter presenter;
  @BindView(R.id.recycler_view) RecyclerView recyclerView;
  @BindString(R.string.search_gifs) String searchGifs;
  @Inject RefWatcher refWatcher;
  @Inject ImageRepository repository;

  //
  // Contract
  //

  @Override public void setPresenter(MainContract.Presenter presenter) {
    this.presenter = presenter;
  }

  @Override public void clearImages() {
    // Clear current data
    adapter.clear();
  }

  @Override public void addImages(RiffsyResponse response) {
    // Iterate over data from response and grab the urls
    for (final Result result : response.getResults()) {
      final String url = result.getMedia().get(0).getGif().getUrl();

      adapter.add(new ImageInfo().withUrl(url));

      if (Log.isLoggable(TAG, Log.INFO)) {
        Log.i(TAG, "ORIGINAL_IMAGE_URL\t" + url);
      }
    }
  }

  @Override public void showDialog(String url) {
    showImageDialog(url);
  }

  @Override public boolean isActive() {
    return isAdded();
  }

  //
  // GifAdapter
  //

  @Override public void onUserItemClicked(ImageInfo imageInfo) {
    showDialog(imageInfo.getUrl());
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
    itemOffsetDecoration = new GifItemDecoration(getActivity(), layoutManager.getSpanCount());
    adapter = new GifAdapter(this, repository);
  }

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    final View view = inflater.inflate(R.layout.fragment_main, container, false);

    // Bind views
    unbinder = ButterKnife.bind(this, view);

    // Setup RecyclerView
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.addItemDecoration(itemOffsetDecoration);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(adapter);
    recyclerView.setHasFixedSize(true);
    recyclerView.getRecycledViewPool().setMaxRecycledViews(0, PORTRAIT_COLUMNS + PORTRAIT_COLUMNS);
    recyclerView.setItemViewCacheSize(RiffsyRepository.DEFAULT_LIMIT_COUNT);
    recyclerView.setDrawingCacheEnabled(true);
    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

    // Custom view for Dialog
    final View dialogView = View.inflate(getContext(), R.layout.dialog_preview, null);

    // Customize Dialog
    dialog = new Dialog(getContext());
    dialog.setContentView(dialogView);
    dialog.setOnDismissListener(dialog1 -> {
      Glide.clear(imageView);
      imageView.setImageDrawable(null);
    });

    // Dialog views
    dialogText = ButterKnife.findById(dialogView, R.id.gif_dialog_title);
    progressBar = ButterKnife.findById(dialogView, R.id.gif_dialog_progress);
    imageView = ButterKnife.findById(dialogView, R.id.gif_dialog_image);

    // Load initial images
    presenter.loadTrendingImages();

    return view;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.menu_fragment_main, menu);

    final MenuItem menuItem = menu.findItem(R.id.menu_search);

    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
    searchView.setQueryHint(searchGifs);

    // Set contextual action on search icon click
    MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
      @Override public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
      }

      @Override public boolean onMenuItemActionCollapse(MenuItem item) {
        // When search is closed, go back to trending results
        if (hasSearched) {
          presenter.loadTrendingImages();
          hasSearched = false;
        }
        return true;
      }
    });

    // Query listener for search bar
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextChange(String newText) {
        // Search on type
        if (!TextUtils.isEmpty(newText)) {
          presenter.loadSearchImages(newText);
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

  @Override public void onDestroyView() {
    super.onDestroyView();

    // Unbind views
    unbinder.unbind();
  }

  @Override public void onDestroy() {
    super.onDestroy();

    refWatcher.watch(this, TAG);
  }

  private void showImageDialog(String url) {
    dialog.show();
    // Remove "white" background for dialog
    dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

    // Load associated text
    dialogText.setText(url);
    dialogText.setVisibility(View.VISIBLE);

    // Load image
    repository.load(url)
      .listener(new RequestListener<Object, GifDrawable>() {
        @Override public boolean onException(Exception e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
          // Show gif
          imageView.setImageResource(R.mipmap.ic_launcher);
          imageView.setVisibility(View.VISIBLE);

          // Hide progressbar
          progressBar.setVisibility(View.GONE);
          if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, "finished loading\t" + model);
          }
          return false;
        }

        @Override public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
          // Show gif
          imageView.setVisibility(View.VISIBLE);

          // Hide progressbar
          progressBar.setVisibility(View.GONE);
          if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, "finished loading\t" + model);
          }
          return false;
        }
      })
      .into(imageView);
  }
}
