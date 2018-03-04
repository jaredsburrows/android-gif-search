tasks.withType<JavaCompile> {
  sourceCompatibility = rootProject.extra["javaVersion"] as String
  targetCompatibility = rootProject.extra["javaVersion"] as String

  // Show all warnings except boot classpath
  options.apply {
    compilerArgs.apply {
      add("-Xlint:all")                // Turn on all warnings
      add("-Xlint:-classfile")         // Ignore Java 8 method param meta data
      add("-Xlint:-unchecked")         // Dagger 2 unchecked issues
      add("-Werror")                   // Turn warnings into errors
    }
    encoding = "utf-8"
    isIncremental = true
    isFork = true
  }
}

tasks.withType<Test> {
  testLogging {
    setExceptionFormat("full")
    showCauses = true
    showExceptions = true
    showStackTraces = true
    events("failed", "skipped")
  }

  failFast = true
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
