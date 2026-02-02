package com.burrowsapps.gif.search.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ArrowBack24: ImageVector
    get() {
        if (_ArrowBack24 != null) {
            return _ArrowBack24!!
        }
        _ArrowBack24 = ImageVector.Builder(
            name = "ArrowBack24",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(313f, 520f)
                lineTo(537f, 744f)
                lineTo(480f, 800f)
                lineTo(160f, 480f)
                lineTo(480f, 160f)
                lineTo(537f, 216f)
                lineTo(313f, 440f)
                lineTo(800f, 440f)
                lineTo(800f, 520f)
                lineTo(313f, 520f)
                close()
            }
        }.build()

        return _ArrowBack24!!
    }

@Suppress("ObjectPropertyName")
private var _ArrowBack24: ImageVector? = null
