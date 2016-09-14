package burrows.apps.giphy.example.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
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
import android.widget.ProgressBar;
import burrows.apps.giphy.example.App;
import burrows.apps.giphy.example.R;
import burrows.apps.giphy.example.rest.model.Data;
import burrows.apps.giphy.example.rest.model.GiphyResponse;
import burrows.apps.giphy.example.rest.service.GiphyService;
import burrows.apps.giphy.example.rx.RxBus;
import burrows.apps.giphy.example.rx.event.PreviewImageEvent;
import burrows.apps.giphy.example.ui.adapter.GiphyAdapter;
import burrows.apps.giphy.example.ui.adapter.ItemOffsetDecoration;
import burrows.apps.giphy.example.ui.adapter.model.GiphyImageInfo;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifDrawableBuilder;
import pl.droidsonroids.gif.GifImageView;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;

/**
 * Main Fragment of the application that displays the Recylcerview of Gif images.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class MainFragment extends Fragment {
  static final String TAG = MainFragment.class.getSimpleName();
  private static final int GIF_IMAGE_HEIGHT_PIXELS = 128;
  private static final int GIF_IMAGE_WIDTH_PIXELS = GIF_IMAGE_HEIGHT_PIXELS;
  private static final int PORTRAIT_COLUMNS = 3;
  private CompositeDisposable compositeDisposable;
  private RecyclerView.LayoutManager layoutManager;
  private ItemOffsetDecoration itemOffsetDecoration;
  private GiphyAdapter adapter;
  private Unbinder unbinder;
  boolean hasSearched;
  private Dialog dialog;
  AppCompatTextView dialogText;
  ProgressBar progressBar;
  GifImageView gifImageView;
  @BindView(R.id.recyclerview_root) RecyclerView recyclerView;
  @BindString(R.string.search_gifs) String searchGifs;
  @Inject RxBus mRxBus;

  @Override public void onStart() {
    super.onStart();

    compositeDisposable.add(mRxBus.toObservable()
                                  .subscribe(event -> {
                                    if (event instanceof PreviewImageEvent) {
                                      showImageDialog((PreviewImageEvent) event);
                                    }
                                  }));
  }

  @Override public void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);

    App.get(getContext()).getAppComponent().inject(this);

    compositeDisposable = new CompositeDisposable();
    layoutManager = new GridLayoutManager(getActivity(), PORTRAIT_COLUMNS);
    itemOffsetDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.gif_adapter_item_offset);
    adapter = new GiphyAdapter();
  }

  @Override public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                     final Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    final View view = inflater.inflate(R.layout.fragment_main, container, false);

    // Bind views
    unbinder = ButterKnife.bind(this, view);

    // Setup RecyclerView
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.addItemDecoration(itemOffsetDecoration);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(adapter);
    recyclerView.getRecycledViewPool().setMaxRecycledViews(0, PORTRAIT_COLUMNS + PORTRAIT_COLUMNS);
    recyclerView.setHasFixedSize(true);
    recyclerView.setItemViewCacheSize(GiphyService.DEFAULT_RESULTS_COUNT);
    recyclerView.setDrawingCacheEnabled(true);
    recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

    // Custom view for Dialog
    final View dialogView = View.inflate(getContext(), R.layout.dialog_preview, null);

    // Customize Dialog
    dialog = new Dialog(getContext());
    dialog.setContentView(dialogView);
    dialog.setOnDismissListener(dialog1 -> {
      Glide.clear(gifImageView);
      gifImageView.setImageDrawable(null);
    });

    // Dialog views
    dialogText = ButterKnife.findById(dialog, R.id.gif_dialog_title);
    progressBar = ButterKnife.findById(dialog, R.id.gif_dialog_progress);
    gifImageView = ButterKnife.findById(dialog, R.id.gif_dialog_image);

    // Load initial images
    loadTrendingImages();

    return view;
  }

  @Override public void onDestroyView() {
    super.onDestroyView();

    // Unbind views
    unbinder.unbind();
  }

  @Override public void onStop() {
    // Unsubscribe from all subscriptions
    compositeDisposable.clear();

    super.onStop();
  }

  @Override public void onDestroy() {
    super.onDestroy();

    App.getRefWatcher(getActivity()).watch(this, TAG);
  }

  @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.menu_fragment_main, menu);

    final MenuItem menuItem = menu.findItem(R.id.menu_search);

    final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
    searchView.setQueryHint(searchGifs);

    // Set contextual action on search icon click
    MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
      @Override public boolean onMenuItemActionExpand(final MenuItem item) {
        return true;
      }

      @Override public boolean onMenuItemActionCollapse(final MenuItem item) {
        // When search is closed, go back to trending results
        if (hasSearched) {
          loadTrendingImages();
          hasSearched = false;
        }
        return true;
      }
    });

    // Query listener for search bar
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextChange(final String newText) {
        // Search on type
        if (!TextUtils.isEmpty(newText)) {
          loadSearchImages(newText);
          hasSearched = true;
        }
        return false;
      }

      @Override public boolean onQueryTextSubmit(final String query) {
        return false;
      }
    });
  }

  /**
   * Load Giphy trending images.
   */
  void loadTrendingImages() {
    loadImages(GiphyService.getInstance().getTrendingResults());
  }

  /**
   * Search Giphy based on user input.
   *
   * @param searchString User input.
   */
  void loadSearchImages(final String searchString) {
    loadImages(GiphyService.getInstance().getSearchResults(searchString));
  }

  /**
   * Common code for subscription.
   *
   * @param observable Observable to added to the subscription.
   */
  private void loadImages(final Observable<GiphyResponse> observable) {
    compositeDisposable.add(observable
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(response -> {
        // onNext

        // Clear current data
        adapter.clear();

        // Iterate over data from response and grab the urls
        for (final Data datum : response.getData()) {
          final String url = datum.getImages().getFixedHeight().getUrl();

          adapter.add(new GiphyImageInfo().withUrl(url));

          if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, "ORIGINAL_IMAGE_URL\t" + url);
          }
        }
      }, error -> {
        // onError
        if (Log.isLoggable(TAG, Log.ERROR)) {
          Log.e(TAG, "onError", error);
        }
      }, () -> {
        // onComplete
        if (Log.isLoggable(TAG, Log.INFO)) {
          Log.i(TAG, "Done loading!");
        }
      }));
  }

  private void showImageDialog(final PreviewImageEvent event) {
    // Get url from event
    final String url = event.getImageInfo().getUrl();

    Glide.with(getContext())
         .load(url)
         .asGif()
         .toBytes()
         .thumbnail(0.1f)
         .override(GIF_IMAGE_WIDTH_PIXELS, GIF_IMAGE_HEIGHT_PIXELS)
         .diskCacheStrategy(DiskCacheStrategy.ALL)
         .error(R.mipmap.ic_launcher)
         .into(new SimpleTarget<byte[]>() {
           @Override public void onResourceReady(final byte[] resource,
                                                 final GlideAnimation<? super byte[]> glideAnimation) {
             // Load gif
             final GifDrawable gifDrawable;
             try {
               gifDrawable = new GifDrawableBuilder().from(resource)
                                                     .build();
               gifImageView.setImageDrawable(gifDrawable);
             } catch (final IOException e) {
               gifImageView.setImageResource(R.mipmap.ic_launcher);
             }
             gifImageView.setVisibility(View.VISIBLE);

             // Load associated text
             dialogText.setText(url);
             dialogText.setVisibility(View.VISIBLE);

             // Turn off progressbar
             progressBar.setVisibility(View.INVISIBLE);
             if (Log.isLoggable(TAG, Log.INFO)) {
               Log.i(TAG, "finished loading\t" + Arrays.toString(resource));
             }
           }
         });

    dialog.show();
  }
}
