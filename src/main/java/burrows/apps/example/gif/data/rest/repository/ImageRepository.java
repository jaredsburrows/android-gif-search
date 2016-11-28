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
  private final int imageHeight;
  private final int imageWidth;
  private final Context context;

  public ImageRepository(Context context) {
    this.context = context;
    this.imageHeight = context.getResources().getDimensionPixelSize(R.dimen.gif_image_width);
    this.imageWidth = imageHeight;
  }

  public <T> GifRequestBuilder<T> load(T url) {
    return Glide.with(context)
      .load(url)
      .asGif()
      .error(R.mipmap.ic_launcher)
      .fallback(R.mipmap.ic_launcher)
      .override(imageWidth, imageHeight)
      // https://github.com/bumptech/glide/issues/600#issuecomment-135541121
      .diskCacheStrategy(DiskCacheStrategy.SOURCE);
  }
}
