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
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifDrawableBuilder;
import pl.droidsonroids.gif.GifImageView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.io.IOException;
import java.util.Arrays;

/**
 * Main Fragment of the application that displays the Recylcerview of Gif images.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class MainFragment extends Fragment {
    private static final String TAG = MainFragment.class.getSimpleName();
    private static final int GIF_IMAGE_HEIGHT_PIXELS = 128;
    private static final int GIF_IMAGE_WIDTH_PIXELS = GIF_IMAGE_HEIGHT_PIXELS;
    private static final int PORTRAIT_COLUMNS = 3;
    private CompositeSubscription mSubscription;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemOffsetDecoration mItemOffsetDecoration;
    private GiphyAdapter mAdapter;
    private Unbinder mUnbinder;
    private boolean mHasSearched;
    private Dialog mDialog;
    private AppCompatTextView mDialogText;
    private ProgressBar mDialogProgressBar;
    private GifImageView mDialogGifImageView;
    @BindView(R.id.recyclerview_root) RecyclerView mRecyclerView;
    @BindString(R.string.search_gifs) String mSearchGifs;

    @Override public void onStart() {
        super.onStart();

        this.mSubscription.add(App.getBus().toObservable()
                                  .subscribe(event -> {
                                      if (event instanceof PreviewImageEvent) {
                                          // Get url from event
                                          final String url = ((PreviewImageEvent) event).getImageInfo().getUrl();

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
                                                           mDialogGifImageView.setImageDrawable(gifDrawable);
                                                       } catch (final IOException e) {
                                                           mDialogGifImageView
                                                               .setImageResource(R.mipmap.ic_launcher);
                                                       }
                                                       mDialogGifImageView.setVisibility(View.VISIBLE);

                                                       // Load associated text
                                                       mDialogText.setText(url);
                                                       mDialogText.setVisibility(View.VISIBLE);

                                                       // Turn off progressbar
                                                       mDialogProgressBar.setVisibility(View.INVISIBLE);
                                                       if (Log.isLoggable(TAG, Log.INFO)) {
                                                           Log.i(TAG,
                                                               "finished loading\t" + Arrays.toString(resource));
                                                       }
                                                   }
                                               });

                                          mDialog.show();
                                      }
                                  }));
    }

    @Override public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setHasOptionsMenu(true);

        this.mSubscription = new CompositeSubscription();
        this.mLayoutManager = new GridLayoutManager(this.getActivity(), PORTRAIT_COLUMNS);
        this.mItemOffsetDecoration = new ItemOffsetDecoration(this.getActivity(), R.dimen.gif_adapter_item_offset);
        this.mAdapter = new GiphyAdapter();
    }

    @Override public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                       final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Bind views
        this.mUnbinder = ButterKnife.bind(this, view);

        // Setup RecyclerView
        this.mRecyclerView.setLayoutManager(this.mLayoutManager);
        this.mRecyclerView.addItemDecoration(this.mItemOffsetDecoration);
        this.mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        this.mRecyclerView.setAdapter(this.mAdapter);
        this.mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, PORTRAIT_COLUMNS + PORTRAIT_COLUMNS);
        this.mRecyclerView.setHasFixedSize(true);
        this.mRecyclerView.setItemViewCacheSize(GiphyService.RESULTS_COUNT);
        this.mRecyclerView.setDrawingCacheEnabled(true);
        this.mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // Custom view for Dialog
        final View dialogView = View.inflate(this.getContext(), R.layout.dialog_preview, null);

        // Customize Dialog
        this.mDialog = new Dialog(this.getContext());
        this.mDialog.setContentView(dialogView);
        this.mDialog.setOnDismissListener(dialog1 -> {
            Glide.clear(mDialogGifImageView);
            mDialogGifImageView.setImageDrawable(null);
        });

        // Dialog views
        this.mDialogText = ButterKnife.findById(this.mDialog, R.id.gif_dialog_title);
        this.mDialogProgressBar = ButterKnife.findById(this.mDialog, R.id.gif_dialog_progress);
        this.mDialogGifImageView = ButterKnife.findById(this.mDialog, R.id.gif_dialog_image);

        // Load initial images
        this.loadTrendingImages();

        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();

        // Unbind views
        this.mUnbinder.unbind();
    }

    @Override public void onStop() {
        // Unsubscribe from all subscriptions
        this.mSubscription.unsubscribe();

        super.onStop();
    }

    @Override public void onDestroy() {
        super.onDestroy();

        App.getRefWatcher(this.getActivity()).watch(this, TAG);
    }

    @Override public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_fragment_main, menu);

        final MenuItem menuItem = menu.findItem(R.id.menu_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint(this.mSearchGifs);

        // Set contextual action on search icon click
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override public boolean onMenuItemActionExpand(final MenuItem item) {
                return true;
            }

            @Override public boolean onMenuItemActionCollapse(final MenuItem item) {
                // When search is closed, go back to trending results
                if (mHasSearched) {
                    loadTrendingImages();
                    mHasSearched = false;
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
                    mHasSearched = true;
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
    private void loadTrendingImages() {
        this.loadImages(GiphyService.getInstance().getTrendingResults());
    }

    /**
     * Search Giphy based on user input.
     *
     * @param searchString User input.
     */
    private void loadSearchImages(final String searchString) {
        this.loadImages(GiphyService.getInstance().getSearchResults(searchString));
    }

    /**
     * Common code for subscription.
     *
     * @param observable Observable to added to the subscription.
     */
    private void loadImages(final Observable<GiphyResponse> observable) {
        this.mSubscription.add(observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(response -> {
                // onNext

                // Clear current data
                this.mAdapter.clear();

                // Iterate over data from response and grab the urls
                for (final Data datum : response.getData()) {
                    final String url = datum.getImages().getFixedHeight().getUrl();

                    this.mAdapter.add(new GiphyImageInfo().withUrl(url));

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
}
