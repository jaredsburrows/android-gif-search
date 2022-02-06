import org.gradle.api.JavaVersion.VERSION_11

plugins {
  id("com.android.library")
  kotlin("android")
  id("org.jlleitschuh.gradle.ktlint")
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

dependencies {
  // Java 8+
  coreLibraryDesugaring(libs.android.desugar)

  // Kotlin
  implementation(platform(libs.kotlin.bom))
  implementation(kotlin("stdlib"))
  implementation(kotlin("stdlib-common"))
  implementation(kotlin("stdlib-jdk7"))
  implementation(kotlin("stdlib-jdk8"))

  implementation(libs.squareup.okhttp.mockwebserver)
}
