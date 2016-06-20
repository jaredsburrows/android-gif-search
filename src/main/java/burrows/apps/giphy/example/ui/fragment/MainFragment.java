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
import burrows.apps.giphy.example.R;
import burrows.apps.giphy.example.event.PreviewImageEvent;
import burrows.apps.giphy.example.rest.model.Data;
import burrows.apps.giphy.example.rest.service.GiphyService;
import burrows.apps.giphy.example.ui.adapter.GiphyAdapter;
import burrows.apps.giphy.example.ui.adapter.ItemOffsetDecoration;
import burrows.apps.giphy.example.ui.adapter.model.GiphyImageInfo;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.felipecsl.gifimageview.library.GifImageView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import java.util.List;

/**
 * Main Fragment of the application that displays the Recylcerview of Gif images.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();
    private static final int GIF_IMAGE_HEIGHT_PIXELS = 175;
    private static final int GIF_IMAGE_WIDTH_PIXELS = GIF_IMAGE_HEIGHT_PIXELS;
    private static final int GIPHY_TRENDING_RESULTS_COUNT = 25;
    private static final int PORTRAIT_COLUMNS = 3;
    private CompositeSubscription mSubscription;
    private RecyclerView.LayoutManager mLayoutManager;
    private ItemOffsetDecoration mItemOffsetDecoration;
    private GiphyAdapter mAdapter;
    private Unbinder mUnbinder;
    private boolean hasSearched;
    @BindView(R.id.recyclerview_root) RecyclerView mRecyclerView;
    @BindString(R.string.search_gifs) String mSearchGifs;

    @Override
    public void onStart() {
        super.onStart();

        // This needs to be 'initiated/registered' here, moving to 'onCreate' has caused weird behaviors
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setHasOptionsMenu(true);

        this.mSubscription = new CompositeSubscription();
        this.mLayoutManager = new GridLayoutManager(this.getActivity(), PORTRAIT_COLUMNS);
        this.mItemOffsetDecoration = new ItemOffsetDecoration(this.getActivity(), R.dimen.gif_adapter_item_offset);
        this.mAdapter = new GiphyAdapter();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
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
        this.mRecyclerView.setItemViewCacheSize(GIPHY_TRENDING_RESULTS_COUNT);
        this.mRecyclerView.setDrawingCacheEnabled(true);
        this.mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // Load initial images
        this.loadTrendingImages();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Unbind views
        this.mUnbinder.unbind();
    }

    @Override
    public void onStop() {
        // Unsubscribe from all subscriptions
        this.mSubscription.unsubscribe();

        // This needs to be 'unregistered' here, moving to 'onDestroy' has caused weird behaviors
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_fragment_main, menu);

        final MenuItem menuItem = menu.findItem(R.id.menu_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint(this.mSearchGifs);

        // Set contextual action on search icon click
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(final MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(final MenuItem item) {
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
            @Override
            public boolean onQueryTextChange(final String newText) {
                // Search on type
                if (!TextUtils.isEmpty(newText)) {
                    searchImages(newText);
                    hasSearched = true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(final String query) {
                return false;
            }
        });
    }

    @Subscribe
    public void onEvent(final PreviewImageEvent event) {

        // Get url from event
        final String url = event.getImageInfo().getUrl();

        // Custom view for Dialog
        final View view = View.inflate(this.getContext(), R.layout.dialog_preview, null);

        // Customize Dialog
        final Dialog dialog = new Dialog(this.getContext());
        dialog.setContentView(view);

        final AppCompatTextView text = ButterKnife.findById(dialog, R.id.gif_dialog_title);
        final ProgressBar progressBar = ButterKnife.findById(dialog, R.id.gif_dialog_progress);
        final GifImageView image = ButterKnife.findById(dialog, R.id.gif_dialog_image);

        Glide.with(this.getContext())
                .load(url)
                .asGif()
                .thumbnail(0.1f)
                .crossFade()
                .listener(new RequestListener<String, GifDrawable>() {
                    @Override
                    public boolean onException(final Exception e, final String model, final Target<GifDrawable> target,
                                               final boolean isFirstResource) {
                        // Update views
                        progressBar.setVisibility(View.INVISIBLE);

                        image.setImageResource(R.mipmap.ic_launcher);
                        image.setVisibility(View.VISIBLE);

                        if (Log.isLoggable(TAG, Log.ERROR)) {
                            Log.e(TAG, "onException", e);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final GifDrawable resource, final String model,
                                                   final Target<GifDrawable> target, final boolean isFromMemoryCache,
                                                   final boolean isFirstResource) {
                        // Update views
                        image.startAnimation();
                        image.setVisibility(View.VISIBLE);

                        text.setText(url);
                        text.setVisibility(View.VISIBLE);

                        progressBar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .override(GIF_IMAGE_WIDTH_PIXELS, GIF_IMAGE_HEIGHT_PIXELS)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.mipmap.ic_launcher)
                .into(image);

        dialog.show();
    }

    /**
     * Load Giphy trending images.
     */
    private void loadTrendingImages() {
        this.mAdapter.clear();

        this.mSubscription.add(GiphyService.getInstance().getTrendingResults()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    // onNext
                    final List<Data> data = response.getData();

                    // Iterate over data from response and grab the urls
                    for (final Data datum : data) {
                        final String url = datum.getImages().getFixedHeight().getUrl();

                        final GiphyImageInfo model = new GiphyImageInfo().withUrl(url);

                        this.mAdapter.add(model);

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

    /**
     * Search Giphy based on user input.
     *
     * @param searchString User input.
     */
    private void searchImages(final String searchString) {
        this.mAdapter.clear();

        this.mSubscription.add(GiphyService.getInstance().getSearchResults(searchString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    // onNext
                    final List<Data> data = response.getData();

                    for (final Data datum : data) {
                        final String url = datum.getImages().getFixedHeight().getUrl();

                        final GiphyImageInfo model = new GiphyImageInfo().withUrl(url);

                        this.mAdapter.add(model);

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
