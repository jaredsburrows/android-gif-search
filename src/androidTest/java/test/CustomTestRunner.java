package test;

import android.app.Application;
import android.content.Context;
import android.support.test.runner.AndroidJUnitRunner;
import burrows.apps.example.gif.TestApp;

/**
 * @author <a href="mailto:jaredsburrows@gmail.com">Jared Burrows</a>
 */
public class CustomTestRunner extends AndroidJUnitRunner {
  @Override public Application newApplication(ClassLoader cl, String className, Context context)
    throws InstantiationException, IllegalAccessException, ClassNotFoundException {
    return super.newApplication(cl, TestApp.class.getName(), context); // Need full path of class
  }
}
