package burrows.apps.gif.example.rest.service;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import burrows.apps.gif.example.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class ImageDownloader {
  private static final int GIF_IMAGE_HEIGHT_PIXELS = 135;
  private static final int GIF_IMAGE_WIDTH_PIXELS = GIF_IMAGE_HEIGHT_PIXELS;
  private static final float THUMBNAIL_MULTIPLIER = 0.1f;
  private final Context context;

  public ImageDownloader(Context context) {
    this.context = context;
  }

  public void load(String url, ImageView imageView, ProgressBar progressBar) {
    Glide.with(context)
      .load(url)
      .asGif()
      .thumbnail(THUMBNAIL_MULTIPLIER)
      .override(GIF_IMAGE_WIDTH_PIXELS, GIF_IMAGE_HEIGHT_PIXELS)
      .diskCacheStrategy(DiskCacheStrategy.SOURCE)
      .listener(new RequestListener<String, GifDrawable>() {
        @Override public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
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

        @Override public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
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
