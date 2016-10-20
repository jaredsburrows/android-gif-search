package burrows.apps.example.gif.rest.service;

import android.content.Context;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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

  public GifRequestBuilder<?> load(Object url) {
    return Glide.with(context)
      .load(url)
      .asGif()
      .thumbnail(THUMBNAIL_MULTIPLIER)
      .override(GIF_IMAGE_WIDTH_PIXELS, GIF_IMAGE_HEIGHT_PIXELS)
      .diskCacheStrategy(DiskCacheStrategy.SOURCE);
  }
}
