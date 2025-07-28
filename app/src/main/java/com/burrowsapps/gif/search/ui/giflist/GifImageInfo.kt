package com.burrowsapps.gif.search.ui.giflist

import androidx.compose.runtime.Immutable

/**
 * Model for the GifAdapter in order to display the gifs.
 */
@Immutable
internal data class GifImageInfo(
  val tinyGifUrl: String = "",
  val tinyGifPreviewUrl: String = "",
  val gifUrl: String = "",
  val gifPreviewUrl: String = "",
)
