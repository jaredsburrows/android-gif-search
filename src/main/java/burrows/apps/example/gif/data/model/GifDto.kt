package burrows.apps.example.gif.data.model

import com.squareup.moshi.Json

data class GifDto(
    @field:Json(name = "url") val _url: String? = null,
    @field:Json(name = "preview") val _preview: String? = null
) {
    val url: String
        get() = _url.orEmpty()
    val preview: String
        get() = _preview.orEmpty()
}
