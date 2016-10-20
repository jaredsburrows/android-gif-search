package burrows.apps.example.gif.data.rest.repository;

import android.content.Context;
import burrows.apps.example.gif.R;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class ImageRepository {
  private static final float THUMBNAIL_MULTIPLIER = 0.1f;
  private final int imageHeight;
  private final int imageWidth;
  private final Context context;

  public ImageRepository(Context context) {
    this.context = context;
    this.imageHeight = context.getResources().getDimensionPixelSize(R.dimen.gif_image_width);
    this.imageWidth = imageHeight;
  }

  public GifRequestBuilder<?> load(Object url) {
    return Glide.with(context)
      .load(url)
      .asGif()
      .thumbnail(THUMBNAIL_MULTIPLIER)
      .override(imageWidth, imageHeight)
      .diskCacheStrategy(DiskCacheStrategy.SOURCE);
  }
}
