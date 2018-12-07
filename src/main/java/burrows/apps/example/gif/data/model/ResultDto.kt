package burrows.apps.example.gif.data.model

import com.squareup.moshi.Json

data class ResultDto(
    @field:Json(name = "media") private val _media: List<MediaDto>? = null,
    @field:Json(name = "title") private val _title: String? = null
) {
    val media: List<MediaDto>
        get() = _media.orEmpty()
    val title: String
        get() = _title.orEmpty()
}
