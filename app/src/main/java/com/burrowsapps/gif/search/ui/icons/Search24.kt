package com.burrowsapps.gif.search.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Search24: ImageVector
    get() {
        if (_Search24 != null) {
            return _Search24!!
        }
        _Search24 = ImageVector.Builder(
            name = "Search24",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(784f, 840f)
                lineTo(532f, 588f)
                quadTo(502f, 612f, 463f, 626f)
                quadTo(424f, 640f, 380f, 640f)
                quadTo(271f, 640f, 195.5f, 564.5f)
                quadTo(120f, 489f, 120f, 380f)
                quadTo(120f, 271f, 195.5f, 195.5f)
                quadTo(271f, 120f, 380f, 120f)
                quadTo(489f, 120f, 564.5f, 195.5f)
                quadTo(640f, 271f, 640f, 380f)
                quadTo(640f, 424f, 626f, 463f)
                quadTo(612f, 502f, 588f, 532f)
                lineTo(840f, 784f)
                lineTo(784f, 840f)
                close()
                moveTo(380f, 560f)
                quadTo(455f, 560f, 507.5f, 507.5f)
                quadTo(560f, 455f, 560f, 380f)
                quadTo(560f, 305f, 507.5f, 252.5f)
                quadTo(455f, 200f, 380f, 200f)
                quadTo(305f, 200f, 252.5f, 252.5f)
                quadTo(200f, 305f, 200f, 380f)
                quadTo(200f, 455f, 252.5f, 507.5f)
                quadTo(305f, 560f, 380f, 560f)
                close()
            }
        }.build()

        return _Search24!!
    }

@Suppress("ObjectPropertyName")
private var _Search24: ImageVector? = null
