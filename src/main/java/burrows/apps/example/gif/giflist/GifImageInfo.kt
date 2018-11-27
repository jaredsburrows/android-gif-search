package burrows.apps.example.gif.giflist

/**
 * Model for the GifAdapter in order to display the gifs.
 */
data class GifImageInfo(
    var _url: String? = null,
    var _previewUrl: String? = null
) {
    val url: String
        get() = _url.orEmpty()
    val previewUrl: String
        get() = _previewUrl.orEmpty()
}
