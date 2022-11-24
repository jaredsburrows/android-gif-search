package com.burrowsapps.gif.search.ui.giflist

import androidx.compose.runtime.Immutable

/**
 * Model for the GifAdapter in order to display the gifs.
 */
@Immutable
internal data class GifImageInfo(
  var tinyGifUrl: String = "",
  var tinyGifPreviewUrl: String = "",
  var gifUrl: String = "",
  var gifPreviewUrl: String = "",
)
