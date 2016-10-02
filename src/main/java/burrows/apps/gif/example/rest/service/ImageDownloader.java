package burrows.apps.gif.example.rest.service;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import burrows.apps.gif.example.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifDrawableBuilder;

import java.io.IOException;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class ImageDownloader {
  private static final int GIF_IMAGE_HEIGHT_PIXELS = 135;
  private static final int GIF_IMAGE_WIDTH_PIXELS = GIF_IMAGE_HEIGHT_PIXELS;
  private static final float THUMBNAIL_MULTIPLIER = 0.1f;
  private Context context;

  public ImageDownloader(Context context) {
    this.context = context;
  }

  public void load(String url, ImageView imageView, ProgressBar progressBar) {
    Glide.with(context)
      .load(url)
      .asGif()
      .toBytes()
      .thumbnail(THUMBNAIL_MULTIPLIER)
      .override(GIF_IMAGE_WIDTH_PIXELS, GIF_IMAGE_HEIGHT_PIXELS)
      .error(R.mipmap.ic_launcher)
      .into(new SimpleTarget<byte[]>() {
        @Override public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> glideAnimation) {
          // Load gif
          final GifDrawable gifDrawable;
          try {
            gifDrawable = new GifDrawableBuilder().from(resource).build();
            imageView.setImageDrawable(gifDrawable);
          } catch (IOException e) {
            imageView.setImageResource(R.mipmap.ic_launcher);
          }
          imageView.setVisibility(View.VISIBLE);

          // Turn off progressbar
          progressBar.setVisibility(View.INVISIBLE);
          if (Log.isLoggable(TAG, Log.INFO)) {
            Log.i(TAG, "finished loading\t" + Arrays.toString(resource));
          }
        }
      });
  }
}
