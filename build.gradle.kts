import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  rootProject.extra["ci"] = rootProject.hasProperty("ci")

  repositories {
    google()
    gradlePluginPortal()
  }

  dependencies {
    classpath(deps.plugin.gradle)
    classpath(deps.plugin.kotlin)
    classpath(deps.plugin.dexcount)
    classpath(deps.plugin.apksize)
    classpath(deps.plugin.versions)
    classpath(deps.plugin.ktlint)
    classpath(deps.plugin.dagger)
  }
}

allprojects {
  repositories {
    google()
    gradlePluginPortal()
  }

  configurations.all {
    resolutionStrategy {
      failOnVersionConflict()

      preferProjectModules()

      eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
          useVersion(deps.versions.kotlin)
        }
      }
    }
  }

  tasks.withType(KotlinCompile::class.java).all {
    kotlinOptions {
      // allWarningsAsErrors = true // migrate ActivityTestRule to ActivityScenario
      jvmTarget = deps.versions.java.toString()
      languageVersion = "1.4"
      apiVersion = "1.4"
      freeCompilerArgs = freeCompilerArgs + listOf(
        "-progressive",
        "-Xjsr305=strict",
        "-Xnew-inference",
        "-Xjvm-default=all",
        "-Xemit-jvm-type-annotations",
        "-Xassertions=jvm",
        "-Xstrict-java-nullability-assertions"
      )
    }
  }

  tasks.withType(JavaCompile::class.java).all {
    sourceCompatibility = deps.versions.java.toString()
    targetCompatibility = deps.versions.java.toString()

    // Show all warnings except boot classpath
    options.apply {
      compilerArgs.apply {
        add("-Xlint:all")                // Turn on all warnings
        add("-Xlint:-deprecation")       // Allow deprecations from Dagger 2
        add("-Xlint:-classfile")         // Ignore Java 8 method param meta data
        add("-Xlint:-unchecked")         // Dagger 2 unchecked issues
        add("-Werror")                   // Turn warnings into errors
      }
      encoding = "utf-8"
      isFork = true
    }
  }

  tasks.withType(Test::class.java).all {
    testLogging {
      exceptionFormat = TestExceptionFormat.FULL
      showCauses = true
      showExceptions = true
      showStackTraces = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
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
}

apply {
  plugin("com.github.ben-manes.versions")
}
