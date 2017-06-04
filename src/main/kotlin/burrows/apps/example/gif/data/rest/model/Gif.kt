package burrows.apps.example.gif.data.rest.model

import com.google.gson.annotations.SerializedName

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class Gif {
    @SerializedName("url") private var url: String? = null
    @SerializedName("preview") private var preview: String? = null

    /**
     * No args constructor for use in serialization
     */
    constructor() : this(Builder())

    constructor(builder: Builder) {
        this.url = builder.url
        this.preview = builder.preview
    }

    fun url(): String? {
        return url
    }

    fun preview(): String? {
        return preview
    }

    fun newBuilder(): Builder {
        return Builder(this)
    }

    class Builder {
        var url: String? = null
        var preview: String? = null

        constructor()

        constructor(gif: Gif) {
            this.url = gif.url
            this.preview = gif.preview
        }

        fun url(url: String): Builder {
            this.url = url
            return this
        }

        fun preview(preview: String): Builder {
            this.preview = preview
            return this
        }

        fun build(): Gif {
            return Gif(this)
        }
    }
}
