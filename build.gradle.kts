import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.JavaVersion.VERSION_17
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.util.Locale

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.dagger) apply false
  alias(libs.plugins.license) apply false
  alias(libs.plugins.ktlint)
  alias(libs.plugins.versions)
  idea
}

idea {
  module {
    isDownloadSources = true
    isDownloadJavadoc = true
  }
}

tasks.withType<Wrapper>().configureEach {
  distributionType = Wrapper.DistributionType.ALL
}

allprojects {
  apply(plugin = "org.jlleitschuh.gradle.ktlint")

  tasks.withType<DependencyUpdatesTask>().configureEach {
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
            // Allow non-stable versions for these groups
            "com.android.application", "org.jetbrains.kotlin" -> {}
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

  tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
      jvmTarget.set(JVM_17)
      freeCompilerArgs.addAll(
        // https://kotlinlang.org/docs/compiler-reference.html#progressive
        "-progressive",
        "-Xjsr305=strict",
        "-Xemit-jvm-type-annotations",
        // https://publicobject.com/2019/11/18/kotlins-assert-is-not-like-javas-assert/
        "-Xassertions=jvm",
        // https://blog.jetbrains.com/kotlin/2020/07/kotlin-1-4-m3-generating-default-methods-in-interfaces/
        "-Xjvm-default=all",
      )
    }
  }

  tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = VERSION_17.toString()
    targetCompatibility = VERSION_17.toString()

    // Show all warnings except boot classpath
    options.apply {
      compilerArgs = compilerArgs +
        listOf(
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

  tasks.withType<Test>().configureEach {
    testLogging {
      exceptionFormat = FULL
      showCauses = true
      showExceptions = true
      showStackTraces = true
      events = setOf(FAILED, SKIPPED)
    }

    // For mockito to work with JDK 21
    jvmArgs("-Dnet.bytebuddy.experimental=true")

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
