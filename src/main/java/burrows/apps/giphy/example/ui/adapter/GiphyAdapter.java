package burrows.apps.giphy.example.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import burrows.apps.giphy.example.R;
import burrows.apps.giphy.example.event.PreviewImageEvent;
import burrows.apps.giphy.example.ui.adapter.model.GiphyImageInfo;
import burrows.apps.giphy.example.ui.fragment.MainFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.felipecsl.gifimageview.library.GifImageView;
import org.greenrobot.eventbus.EventBus;

/**
 * RecyclerView adapter for handling Gif Images in a Grid format.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class GiphyAdapter extends BaseAdapter<GiphyImageInfo, GiphyAdapter.GiphyAdapterViewHolder> {

    private static final String TAG = MainFragment.class.getSimpleName();
    private static final int GIF_IMAGE_HEIGHT_PIXELS = 128;
    private static final int GIF_IMAGE_WIDTH_PIXELS = GIF_IMAGE_HEIGHT_PIXELS;

    @Override
    public GiphyAdapterViewHolder onCreateViewHolder(final ViewGroup parent, final int position) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_list_item, parent, false);
        return new GiphyAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GiphyAdapterViewHolder holder, final int position) {

        final Context context = holder.mGifImageView.getContext();
        final GiphyImageInfo model = this.getItem(position);

        Glide.with(context)
                .load(model.getUrl())
                .asGif()
                .thumbnail(0.1f)
                .crossFade()
                .listener(new RequestListener<String, GifDrawable>() {
                    @Override
                    public boolean onException(final Exception e, final String model, final Target<GifDrawable> target,
                                               final boolean isFirstResource) {
                        // Update views
                        holder.mProgressBar.setVisibility(View.INVISIBLE);

                        holder.mGifImageView.setImageResource(R.mipmap.ic_launcher);
                        holder.mGifImageView.setVisibility(View.VISIBLE);

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
                        holder.mGifImageView.startAnimation();
                        holder.mGifImageView.setVisibility(View.VISIBLE);

                        holder.mProgressBar.setVisibility(View.INVISIBLE);
                        if (Log.isLoggable(TAG, Log.INFO)) {
                            Log.i(TAG, "finished loading\t" + model);
                        }
                        return false;
                    }
                })
                .override(GIF_IMAGE_WIDTH_PIXELS, GIF_IMAGE_HEIGHT_PIXELS)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.mipmap.ic_launcher)
                .into(holder.mGifImageView);

        holder.itemView.setOnClickListener(view -> {
            final PreviewImageEvent event = new PreviewImageEvent(model);

            EventBus.getDefault().post(event);
        });
    }

    @Override
    public void onViewRecycled(final GiphyAdapterViewHolder holder) {
        super.onViewRecycled(holder);

        Glide.clear(holder.mGifImageView);
    }

    /**
     * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
     */
    public class GiphyAdapterViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.gif_progress) ProgressBar mProgressBar;
        @BindView(R.id.gif_image) GifImageView mGifImageView;

        public GiphyAdapterViewHolder(final View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }
}
