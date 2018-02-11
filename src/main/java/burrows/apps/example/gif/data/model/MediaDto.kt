package burrows.apps.example.gif.data.model

import com.squareup.moshi.Json

data class MediaDto(
  @field:Json(name = "tinygif") var gif: GifDto? = GifDto("", "")) // Bug in 1.5.0
