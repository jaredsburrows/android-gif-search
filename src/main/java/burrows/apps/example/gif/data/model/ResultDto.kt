package burrows.apps.example.gif.data.model

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
data class ResultDto(
  var media: List<MediaDto>? = emptyList(),
  var title: String? = "")
