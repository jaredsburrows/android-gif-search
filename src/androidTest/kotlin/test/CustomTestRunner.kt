package test

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner
import burrows.apps.example.gif.TestApp

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class CustomTestRunner : AndroidJUnitRunner() {
  @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
  override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
    return super.newApplication(cl, TestApp::class.java.name, context) // Need full path of class
  }
}
