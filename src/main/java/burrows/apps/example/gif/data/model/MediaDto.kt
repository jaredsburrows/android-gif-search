package burrows.apps.example.gif.data.model

import com.squareup.moshi.Json

data class MediaDto(
    @field:Json(name = "tinygif") private val gifJson: GifDto? = null // Bug in 1.5.0
) {
    val gif: GifDto
        get() = gifJson ?: GifDto("", "")
}
