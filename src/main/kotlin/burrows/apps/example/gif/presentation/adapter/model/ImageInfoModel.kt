package burrows.apps.example.gif.presentation.adapter.model

/**
 * Model for the GifAdapter in order to display the gifs.
 *
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
data class ImageInfoModel(var url: String? = null,
                          var previewUrl: String? = null) {
  constructor() : this(null, null)
}
