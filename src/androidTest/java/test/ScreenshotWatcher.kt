package test

import android.graphics.Bitmap
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.IOException

class ScreenshotWatcher : TestWatcher() {
    private lateinit var description: Description

    override fun apply(base: Statement, description: Description): Statement {
        this.description = description
        return super.apply(base, description)
    }

    override fun succeeded(description: Description) {
        capture("succeeded")
    }

    override fun failed(e: Throwable, description: Description) {
        capture("failed")
    }

    fun capture(description: String = "") {
        screenCapture("${this.description.testClass.simpleName}_" +
            "${this.description.methodName}_" +
            description.replace("\\s+", "_")
        )
    }

    private fun screenCapture(fileName: String) {
        try {
            Screenshot.capture().apply {
                format = Bitmap.CompressFormat.PNG
                name = fileName
            }.process()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }
}
