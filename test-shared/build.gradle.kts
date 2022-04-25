import org.gradle.api.JavaVersion.VERSION_11
import java.net.URL

plugins {
  alias(libs.plugins.android.library)
  kotlin("android")
  alias(libs.plugins.ktlint)
}

android {
  compileSdk = libs.versions.sdk.compile.get().toInt()

  defaultConfig {
    minSdk = libs.versions.sdk.min.get().toInt()
    targetSdk = libs.versions.sdk.target.get().toInt()
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = VERSION_11
    targetCompatibility = VERSION_11
  }

  kotlinOptions {
    jvmTarget = VERSION_11.toString()
  }

  lint {
    textReport = true
    checkAllWarnings = true
    warningsAsErrors = true
    lintConfig = file("${project.rootDir}/config/lint/lint.xml")
    checkReleaseBuilds = false
    checkTestSources = true
    abortOnError = true
  }

  packagingOptions {
    resources.excludes += listOf(
      "**/*.kotlin_module",
      "**/*.version",
      "**/kotlin/**",
      "**/*.txt",
      "**/*.xml",
      "**/*.properties",
    )
  }
}

tasks.register("updateTestFiles") {
  doLast {
    // test-shared/src/main/resources
    val resourcesFolder = android.sourceSets["main"].resources.srcDirs.first()

    mapOf(
      // Show enough to emulate a filtered "search" for testing
      "search_results.json" to
        "https://g.tenor.com/v1/search?key=LIVDSRZULELA&media_filter=minimal&q=hello&limit=10",
      // Show just enough to fill the screen for testing
      "trending_results.json" to
        "https://g.tenor.com/v1/trending?key=LIVDSRZULELA&media_filter=minimal&limit=24"
    ).forEach { (file, url) ->
      File(resourcesFolder, file)
        .writeText(
          URL(url)
            .readText()
            // Point our mock JSON to point to local OkHTTP Mock server
            .replace("media.tenor.com", "localhost:8080")
            // Enforce HTTPS
            .replace("http:", "https:")
        )
    }
  }
}

dependencies {
  // Java 8+
  coreLibraryDesugaring(libs.android.desugar)

  // Kotlin
  implementation(platform(libs.kotlin.bom))
  implementation(kotlin("stdlib"))
  implementation(kotlin("stdlib-common"))
  implementation(kotlin("stdlib-jdk7"))
  implementation(kotlin("stdlib-jdk8"))

  // Kotlin X
  implementation(platform(libs.kotlinx.coroutines.bom))
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.coroutines.corejvm)

  implementation(libs.squareup.okhttp.mockwebserver)
}
