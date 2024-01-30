import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.JavaVersion.VERSION_11
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import java.util.Locale
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

rootProject.apply {
  extra["release"] = hasProperty("release")
}

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  kotlin("android") version (libs.versions.kotlin.get()) apply false
  kotlin("jvm") version (libs.versions.kotlin.get()) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.dagger) apply false
  alias(libs.plugins.ktlint) apply false
  alias(libs.plugins.license) apply false
  alias(libs.plugins.versions)
}

allprojects {
  configurations.configureEach {
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
        listOf("RELEASE", "FINAL", "GA").any { version.uppercase(Locale.getDefault()).contains(it) }
      val regex = "^[0-9,.v-]+(-r)?$".toRegex()
      val isStable = stableKeyword || regex.matches(version)
      return isStable.not()
    }

    resolutionStrategy {
      componentSelection {
        all {
          when (candidate.group) {
            "androidx.compose.compiler", "com.android.application", "org.jetbrains.kotlin" -> {}
            else -> {
              if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
                reject("Release candidate")
              }
            }
          }
        }
      }
    }
  }

  tasks.withType(KotlinJvmCompile::class.java).configureEach {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_11)
      languageVersion.set(KotlinVersion.KOTLIN_1_9)
      apiVersion.set(KotlinVersion.KOTLIN_1_9)
      freeCompilerArgs.addAll(
        // https://kotlinlang.org/docs/compiler-reference.html#progressive
        "-progressive",
        "-Xjsr305=strict",
        "-Xemit-jvm-type-annotations",
        // https://publicobject.com/2019/11/18/kotlins-assert-is-not-like-javas-assert/
        "-Xassertions=jvm",
        "-Xproper-ieee754-comparisons",
        // https://blog.jetbrains.com/kotlin/2020/07/kotlin-1-4-m3-generating-default-methods-in-interfaces/
        "-Xjvm-default=all",
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=${libs.versions.kotlin.get()}",
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
        // Ignore "warning: No processor claimed any of these annotations"
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

  tasks.withType(Wrapper::class.java).configureEach {
    distributionType = Wrapper.DistributionType.ALL
  }
}
