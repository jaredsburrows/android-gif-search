package com.burrowsapps.example.gif.data.model

import com.squareup.moshi.Json

data class GifDto(
    @field:Json(name = "url") private val urlJson: String? = null,
    @field:Json(name = "preview") private val urlPreview: String? = null
) {
    val url: String
        get() = urlJson.orEmpty()
    val preview: String
        get() = urlPreview.orEmpty()
}
