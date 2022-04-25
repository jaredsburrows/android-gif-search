import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.JavaVersion.VERSION_11
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

rootProject.apply {
  extra["ci"] = hasProperty("ci")
  extra["release"] = hasProperty("release")
}

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  kotlin("android") version(libs.versions.kotlin.get()) apply false
  kotlin("jvm") version(libs.versions.kotlin.get()) apply false
  alias(libs.plugins.dagger) apply false
  alias(libs.plugins.ktlint) apply false
  alias(libs.plugins.license) apply false
  alias(libs.plugins.versions)
}

allprojects {
  configurations.all {
    resolutionStrategy {
      preferProjectModules()

      enableDependencyVerification()

      eachDependency {
        when (requested.group) {
          "org.jetbrains.kotlin" -> useVersion(libs.versions.kotlin.get())
          "com.google.dagger" -> useVersion(libs.versions.dagger.get())
        }
      }
    }
  }

  tasks.withType(DependencyUpdatesTask::class.java).configureEach {
    fun isNonStable(version: String): Boolean {
      val stableKeyword =
        listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
      val regex = "^[0-9,.v-]+(-r)?$".toRegex()
      val isStable = stableKeyword || regex.matches(version)
      return isStable.not()
    }

    resolutionStrategy {
      componentSelection {
        all {
          if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
            reject("Release candidate")
          }
        }
      }
    }
  }

  tasks.withType(KotlinCompile::class.java).configureEach {
    sourceCompatibility = VERSION_11.toString()
    targetCompatibility = VERSION_11.toString()

    kotlinOptions {
      jvmTarget = VERSION_11.toString()
      languageVersion = "1.6"
      apiVersion = "1.6"
      freeCompilerArgs = freeCompilerArgs + listOf(
        // https://kotlinlang.org/docs/compiler-reference.html#progressive
        "-progressive",
        "-Xjsr305=strict",
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-Xemit-jvm-type-annotations",
        // https://publicobject.com/2019/11/18/kotlins-assert-is-not-like-javas-assert/
        "-Xassertions=jvm",
        "-Xproper-ieee754-comparisons",
        // https://blog.jetbrains.com/kotlin/2020/07/kotlin-1-4-m3-generating-default-methods-in-interfaces/
        "-Xjvm-default=all",
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true",
      )
    }
  }

  tasks.withType(JavaCompile::class.java).configureEach {
    sourceCompatibility = VERSION_11.toString()
    targetCompatibility = VERSION_11.toString()

    // Show all warnings except boot classpath
    options.apply {
      compilerArgs = compilerArgs + listOf(
        // Turn on all warnings
        "-Xlint:all",
        // Allow deprecations from Dagger 2
        "-Xlint:-deprecation",
        // Ignore Java 8 method param meta data
        "-Xlint:-classfile",
        // Dagger 2 unchecked issues
        "-Xlint:-unchecked",
        // Ignore no process
        "-Xlint:-processing",
      )
      compilerArgs.addAll(listOf("-Xmaxerrs", "10000", "-Xmaxwarns", "10000"))
      encoding = "utf-8"
      isFork = true
    }
  }

  tasks.withType(Test::class.java).configureEach {
    testLogging {
      exceptionFormat = FULL
      showCauses = true
      showExceptions = true
      showStackTraces = true
      events = setOf(FAILED, SKIPPED)
    }

    val maxWorkerCount = gradle.startParameter.maxWorkerCount
    maxParallelForks = if (maxWorkerCount < 2) 1 else maxWorkerCount / 2
  }

  tasks.configureEach {
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
