import java.net.URI

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.ktlint)
}

android {
  namespace = "com.burrowsapps.gif.search.test.shared"
}

tasks.register("updateTestFiles") {
  description = "Updates the test JSON files with fresh data from the Klipy API."

  // Resolve the output dir at configuration time so the doLast lambda captures only a
  // File (configuration-cache safe), not the Project via android.sourceSets.
  val resourcesFolder = layout.projectDirectory.dir("src/main/resources").asFile
  doLast {
    val apiKey = "bA9nOCYnCLJhSswsDyJQcGMiigKFLuh2WaZRjzZF8ZfTDxLIrph5CGcHspLqZo9n"
    val testData =
      mapOf(
        // Show enough to emulate a filtered "search" for testing
        "search_results.json" to
          "https://api.klipy.com/api/v1/$apiKey/gifs/search?q=hello&page=1&per_page=1",
        // Show just enough to fill the screen for testing
        "trending_results.json" to
          "https://api.klipy.com/api/v1/$apiKey/gifs/trending?page=1&per_page=1",
      )

    testData.forEach { (file, url) ->
      val jsonContent =
        URI(url)
          .toURL()
          .readText()
          // Point our mock JSON to point to local OkHTTP Mock server
          .replace("static.klipy.com", "localhost:8080")
          // Enforce HTTP for local MockWebServer
          .replace("https:", "http:")

      File(resourcesFolder, file).writeText(jsonContent)
    }
  }
}

dependencies {
  // Kotlin
  ktlintRuleset(libs.ktlint.compose.ruleset)

  // Kotlin X
  implementation(platform(libs.kotlinx.coroutines.bom))
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)

  // OkHTTP
  implementation(platform(libs.squareup.okhttp.bom))
  implementation(libs.squareup.okhttp)
  implementation(libs.squareup.okhttp.mockwebserver)
}
