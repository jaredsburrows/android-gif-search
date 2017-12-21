import org.gradle.api.tasks.testing.logging.TestExceptionFormat

// Turn on all warnings and errors
tasks.withType<JavaCompile> {
  sourceCompatibility = rootProject.extra["javaVersion"] as String
  targetCompatibility = rootProject.extra["javaVersion"] as String

  // Show all warnings except boot classpath
  options.apply {
    compilerArgs.apply {
      add("-Xlint:all")                // Turn on all warnings
      add("-Xlint:-options")           // Turn off "missing" bootclasspath warning
      add("-Xlint:-path")              // Turn off warning - annotation processing
      add("-Xlint:-processing")        // Turn off warning about not claiming annotations
      add("-Xlint:-classfile")         // Ignore Java 8 method param meta data
      add("-Werror")                   // Turn warnings into errors
    }
    encoding = "utf-8"
    isIncremental = true
    isFork = true
  }
}

// Turn on logging for all tests, filter to show failures/skips only
tasks.withType<Test> {
  testLogging {
    exceptionFormat = TestExceptionFormat.FULL
    showCauses = true
    showExceptions = true
    showStackTraces = true
    events("failed", "skipped")
  }

  val maxWorkerCount = gradle.startParameter.maxWorkerCount
  maxParallelForks = if (maxWorkerCount < 2) 1 else maxWorkerCount / 2
}

tasks.all {
  when (this) {
    is JavaForkOptions -> {
      // should improve memory on a 64bit JVM
      jvmArgs("-XX:+UseCompressedOops")
      // should avoid GradleWorkerMain to steal focus
      jvmArgs("-Djava.awt.headless=true")
      jvmArgs("-Dapple.awt.UIElement=true")
    }
  }
}
