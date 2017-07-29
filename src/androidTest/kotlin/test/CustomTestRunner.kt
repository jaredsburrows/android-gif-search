package test

import android.os.Bundle
import android.support.multidex.MultiDex
import android.support.test.runner.AndroidJUnitRunner

/**
 * @author [Jared Burrows](mailto:jaredsburrows@gmail.com)
 */
class CustomTestRunner : AndroidJUnitRunner() {
  override fun onCreate(arguments: Bundle?) {
    MultiDex.installInstrumentation(context, targetContext)
    super.onCreate(arguments)
  }
}
