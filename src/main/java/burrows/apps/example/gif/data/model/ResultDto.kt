package burrows.apps.example.gif.data.model

import com.squareup.moshi.Json

data class ResultDto(
    @field:Json(name = "media") var _media: List<MediaDto>? = null,
    @field:Json(name = "title") var _title: String? = null
) {
    val media: List<MediaDto>
        get() = _media.orEmpty()
    val title: String
        get() = _title.orEmpty()
}
