import org.gradle.api.tasks.testing.logging.TestExceptionFormat

// Turn on all warnings and errors
tasks.withType<JavaCompile> {
  sourceCompatibility = rootProject.extra["javaVersion"] as String
  targetCompatibility = rootProject.extra["javaVersion"] as String

  // Show all warnings except boot classpath
  options.compilerArgs.add("-Xlint:all")                // Turn on all warnings
  options.compilerArgs.add("-Xlint:-options")           // Turn off "missing" bootclasspath warning
  options.compilerArgs.add("-Xlint:-path")              // Turn off warning - annotation processing
  options.compilerArgs.add("-Xlint:-processing")        // Turn off warning about not claiming annotations
  options.compilerArgs.add("-Xdiags:verbose")           // Turn on verbose errors
  options.compilerArgs.add("-Werror")                   // Turn warnings into errors
  options.encoding = "utf-8"
  options.isIncremental = true
  options.isFork = true
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
