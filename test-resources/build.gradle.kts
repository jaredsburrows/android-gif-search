import org.gradle.api.JavaVersion.VERSION_17
import java.net.URL

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.ktlint)
}

val sdkVersion = libs.versions.sdk.get().toInt()
val jvmVersion = VERSION_17

android {
  namespace = "com.burrowsapps.gif.search.test.shared"
  compileSdk = sdkVersion

  defaultConfig {
    minSdk = sdkVersion
  }

  compileOptions {
    sourceCompatibility = jvmVersion
    targetCompatibility = jvmVersion
  }

  kotlinOptions {
    jvmTarget = jvmVersion.toString()
  }

  lint {
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

  packaging {
    resources.excludes +=
      listOf(
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

    val testData =
      mapOf(
        // Show enough to emulate a filtered "search" for testing
        "search_results.json" to
          "https://g.tenor.com/v1/search?key=LIVDSRZULELA&media_filter=minimal&q=hello&limit=1",
        // Show just enough to fill the screen for testing
        "trending_results.json" to
          "https://g.tenor.com/v1/trending?key=LIVDSRZULELA&media_filter=minimal&limit=1",
      )

    testData.forEach { (file, url) ->
      val jsonContent =
        URL(url).readText()
          // Point our mock JSON to point to local OkHTTP Mock server
          .replace("media.tenor.com", "localhost:8080")
          .replace("tenor.com", "localhost:8080")
          // Enforce HTTP for local MockWebServer
          .replace("https:", "http:")

      File(resourcesFolder, file).writeText(jsonContent)
    }
  }
}

dependencies {
  // Kotlin
  implementation(platform(libs.kotlin.bom))

  // Kotlin X
  implementation(platform(libs.kotlinx.coroutines.bom))
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)

  // OkHTTP
  implementation(libs.squareup.okhttp.mockwebserver)
}
