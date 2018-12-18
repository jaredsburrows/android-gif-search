package burrows.apps.example.gif.data.model

import com.squareup.moshi.Json

data class ResultDto(
    @field:Json(name = "media") private val mediaJson: List<MediaDto>? = null,
    @field:Json(name = "title") private val titleJson: String? = null
) {
    val media: List<MediaDto>
        get() = mediaJson.orEmpty()
    val title: String
        get() = titleJson.orEmpty()
}
