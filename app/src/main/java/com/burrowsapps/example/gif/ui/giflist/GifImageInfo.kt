package com.burrowsapps.example.gif.ui.giflist

/**
 * Model for the GifAdapter in order to display the gifs.
 */
data class GifImageInfo(
  var tinyGifUrl: String = "",
  var tinyGifPreviewUrl: String = "",
  var gifUrl: String = "",
  var gifPreviewUrl: String = ""
)
