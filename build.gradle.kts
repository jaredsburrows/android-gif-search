import com.android.build.api.dsl.CommonExtension
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.gradle.api.JavaVersion.VERSION_21
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML
import java.util.Locale

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
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
  distributionType = Wrapper.DistributionType.BIN
}

tasks.withType<DependencyUpdatesTask>().configureEach {
  fun isNonStable(version: String): Boolean {
    val stableKeyword =
      listOf("RELEASE", "FINAL", "GA")
        .any { version.uppercase(Locale.getDefault()).contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return !isStable
  }

  rejectVersionIf {
    val allowNonStableForGroup =
      candidate.group in setOf("com.android.application", "org.jetbrains.kotlin")
    if (allowNonStableForGroup) {
      false
    } else {
      isNonStable(candidate.version) && !isNonStable(currentVersion)
    }
  }
}

subprojects {
  plugins.withId("org.jlleitschuh.gradle.ktlint") {
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
      reporters {
        reporter(HTML)
      }
    }
  }

  plugins.withId("com.android.base") {
    val sdkVersion =
      libs.versions.sdk
        .get()
        .toInt()

    extensions.configure<CommonExtension> {
      compileSdk {
        version =
          release(sdkVersion) {
            minorApiLevel = 1
          }
      }

      defaultConfig.apply {
        minSdk {
          version = release(sdkVersion)
        }
      }

      compileOptions.apply {
        sourceCompatibility = VERSION_21
        targetCompatibility = VERSION_21
      }

      lint.apply {
        abortOnError = true
        checkAllWarnings = true
        warningsAsErrors = true
        checkTestSources = true
        checkDependencies = true
        checkReleaseBuilds = false
        lintConfig = rootDir.resolve("config/lint/lint.xml")
        textReport = true
        sarifReport = true
      }

      packaging.resources.excludes +=
        listOf(
          "**/*.kotlin_module",
          "**/*.version",
          "**/kotlin/**",
          "**/*.txt",
          "**/*.xml",
          "**/*.properties",
        )
    }

    tasks.withType<KotlinJvmCompile>().configureEach {
      compilerOptions {
        jvmTarget.set(JVM_21)
        freeCompilerArgs.addAll(
          // https://kotlinlang.org/docs/compiler-reference.html#progressive
          "-progressive",
          "-Xjsr305=strict",
          "-Xemit-jvm-type-annotations",
          // https://publicobject.com/2019/11/18/kotlins-assert-is-not-like-javas-assert/
          "-Xassertions=jvm",
          // Generate JVM default methods for Kotlin interfaces
          "-jvm-default=enable",
          "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
          "-opt-in=kotlinx.coroutines.FlowPreview",
        )
      }
    }

    tasks.withType<JavaCompile>().configureEach {
      sourceCompatibility = VERSION_21.toString()
      targetCompatibility = VERSION_21.toString()

      // Show all warnings except boot classpath
      options.apply {
        compilerArgs.addAll(
          listOf(
            // Turn on all warnings
            "-Xlint:all",
            // Ignore "warning: No processor claimed any of these annotations"
            "-Xlint:-processing",
            "-Xmaxerrs",
            "10000",
            "-Xmaxwarns",
            "10000",
          ),
        )
        encoding = "utf-8"
      }
    }

    // Attach mockito agent for tests to work with JDK 21+
    val mockitoAgent = configurations.create("mockitoAgent")
    dependencies {
      add("mockitoAgent", libs.mockito)
    }
    tasks.withType<Test>().configureEach {
      testLogging {
        exceptionFormat = FULL
        showCauses = true
        showExceptions = true
        showStackTraces = true
        events = setOf(FAILED, SKIPPED)
      }

      maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

      val agentFiles = mockitoAgent.incoming.files
      jvmArgumentProviders.add(
        CommandLineArgumentProvider {
          listOf("-javaagent:${agentFiles.single { it.name.startsWith("mockito-core") }}")
        },
      )
    }
  }

  // Compose/Paging opt-in markers only exist in :app; opting in here (com.android.application,
  // not com.android.base) avoids unresolved-marker warnings in the :test-resources library.
  plugins.withId("com.android.application") {
    tasks.withType<KotlinJvmCompile>().configureEach {
      compilerOptions {
        freeCompilerArgs.addAll(
          "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
          "-opt-in=androidx.paging.ExperimentalPagingApi",
        )
      }
    }
  }
}
