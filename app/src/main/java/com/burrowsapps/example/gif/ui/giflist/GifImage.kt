package com.burrowsapps.example.gif.ui.giflist

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.components.ImageComponent
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.glide.GlideImageState

@Composable
fun GlideGifImage(
  imageModel: Any?,
  modifier: Modifier = Modifier,
  requestBuilder: RequestBuilder<GifDrawable>,
  component: ImageComponent = rememberImageComponent {},
  imageOptions: ImageOptions = ImageOptions(),
  loading: @Composable (BoxScope.(imageState: GlideImageState.Loading) -> Unit)? = null,
) {
  val LocalGlideGifRequestBuilder: ProvidableCompositionLocal<RequestBuilder<GifDrawable>?> =
    staticCompositionLocalOf { null }

  CompositionLocalProvider(LocalGlideGifRequestBuilder provides requestBuilder) {
    GlideImage(
      imageModel = imageModel,
      modifier = modifier,
      component = component,
      loading = loading,
      imageOptions = imageOptions,
    )
  }
}
