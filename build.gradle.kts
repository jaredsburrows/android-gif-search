import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  rootProject.extra["ci"] = rootProject.hasProperty("ci")

  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
  }

  dependencies {
    classpath(deps.plugin.gradle)
    classpath(deps.plugin.kotlin)
    classpath(deps.plugin.command)
    classpath(deps.plugin.dexcount)
    classpath(deps.plugin.apksize)
    classpath(deps.plugin.versions)
    classpath(deps.plugin.ktlint)
    classpath(deps.plugin.dagger)
  }
}

allprojects {
  apply {
    plugin("com.github.ben-manes.versions")
  }

  configurations.all {
    resolutionStrategy {
      failOnVersionConflict()

      preferProjectModules()

      eachDependency {
        when (requested.group) {
          "org.jetbrains.kotlin" -> useVersion(deps.versions.kotlin)
          "com.google.dagger" -> useVersion(deps.versions.dagger)
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
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()

    kotlinOptions {
      // allWarningsAsErrors = true
      jvmTarget = JavaVersion.VERSION_11.toString()
      languageVersion = "1.6"
      apiVersion = "1.6"
      freeCompilerArgs = freeCompilerArgs + listOf(
        // https://kotlinlang.org/docs/compiler-reference.html#progressive
        "-progressive",
        "-Xjsr305=strict",
        "-Xemit-jvm-type-annotations",
        // Match JVM assertion behavior: https://publicobject.com/2019/11/18/kotlins-assert-is-not-like-javas-assert/
        "-Xassertions=jvm",
        "-Xproper-ieee754-comparisons",
        // Generate nullability assertions for non-null Java expressions
        "-Xstrict-java-nullability-assertions",
        // Enable new jvmdefault behavior
        // https://blog.jetbrains.com/kotlin/2020/07/kotlin-1-4-m3-generating-default-methods-in-interfaces/
        "-Xjvm-default=all", // "-Xjvm-default=enable",
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true",
      )
    }
  }

  tasks.withType(JavaCompile::class.java).configureEach {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()

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
        // Turn warnings into errors
        "-Werror",
      )
      compilerArgs.addAll(listOf("-Xmaxerrs", "10000", "-Xmaxwarns", "10000"))
      encoding = "utf-8"
      isFork = true
    }
  }

  tasks.withType(Test::class.java).configureEach {
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
