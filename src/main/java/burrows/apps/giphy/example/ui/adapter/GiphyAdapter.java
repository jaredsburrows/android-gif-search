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
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import org.greenrobot.eventbus.EventBus;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifDrawableBuilder;
import pl.droidsonroids.gif.GifImageView;

import java.io.IOException;

/**
 * RecyclerView adapter for handling Gif Images in a Grid format.
 *
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public final class GiphyAdapter extends BaseAdapter<GiphyImageInfo, GiphyAdapter.GiphyAdapterViewHolder> {
    private static final String TAG = MainFragment.class.getSimpleName();
    private static final int GIF_IMAGE_HEIGHT_PIXELS = 128;
    private static final int GIF_IMAGE_WIDTH_PIXELS = GIF_IMAGE_HEIGHT_PIXELS;

    @Override public GiphyAdapterViewHolder onCreateViewHolder(final ViewGroup parent, final int position) {
        return new GiphyAdapterViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_list_item, parent, false));
    }

    @Override public void onBindViewHolder(final GiphyAdapterViewHolder holder, final int position) {
        final Context context = holder.mGifImageView.getContext();
        final GiphyImageInfo model = this.getItem(position);

        Glide.with(context)
                .load(model.getUrl())
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
                            gifDrawable = new GifDrawableBuilder().from(resource).build();
                            holder.mGifImageView.setImageDrawable(gifDrawable);
                        } catch (final IOException e) {
                            holder.mGifImageView.setImageResource(R.mipmap.ic_launcher);
                        }
                        holder.mGifImageView.setVisibility(View.VISIBLE);

                        // Turn off progressbar
                        holder.mProgressBar.setVisibility(View.INVISIBLE);
                        if (Log.isLoggable(TAG, Log.INFO)) {
                            Log.i(TAG, "finished loading\t" + model);
                        }
                    }
                });


//        Glide.with(context)
//                .load(model.getUrl())
//                .asGif()
//                .thumbnail(0.1f)
//                .crossFade()
//                .listener(new RequestListener<String, GifDrawable>() {
//                    @Override
//                    public boolean onException(final Exception e, final String model, final Target<GifDrawable> target,
//                                               final boolean isFirstResource) {
//                        // Update views
//                        holder.mProgressBar.setVisibility(View.INVISIBLE);
//
//                        holder.mGifImageView.setImageResource(R.mipmap.ic_launcher);
//                        holder.mGifImageView.setVisibility(View.VISIBLE);
//
//                        if (Log.isLoggable(TAG, Log.ERROR)) {
//                            Log.e(TAG, "onException", e);
//                        }
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(final GifDrawable resource, final String model,
//                                                   final Target<GifDrawable> target, final boolean isFromMemoryCache,
//                                                   final boolean isFirstResource) {
//                        // Update views
//                        holder.mGifImageView.startAnimation();
//                        holder.mGifImageView.setVisibility(View.VISIBLE);
//
//                        holder.mProgressBar.setVisibility(View.INVISIBLE);
//                        if (Log.isLoggable(TAG, Log.INFO)) {
//                            Log.i(TAG, "finished loading\t" + model);
//                        }
//                        return false;
//                    }
//                })
//                .override(GIF_IMAGE_WIDTH_PIXELS, GIF_IMAGE_HEIGHT_PIXELS)
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .error(R.mipmap.ic_launcher)
//                .into(holder.mGifImageView);

        holder.itemView.setOnClickListener(view -> EventBus.getDefault().post(new PreviewImageEvent(model)));
    }

    @Override public void onViewRecycled(final GiphyAdapterViewHolder holder) {
        super.onViewRecycled(holder);

        Glide.clear(holder.mGifImageView);
        holder.mGifImageView.setImageDrawable(null);
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
