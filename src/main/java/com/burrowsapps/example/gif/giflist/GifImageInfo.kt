package com.burrowsapps.example.gif.giflist

/**
 * Model for the GifAdapter in order to display the gifs.
 */
data class GifImageInfo(
    var inUrl: String? = "",
    var inPreviewUrl: String? = ""
) {
    val url: String
        get() = inUrl.orEmpty()
    val previewUrl: String
        get() = inPreviewUrl.orEmpty()
}
