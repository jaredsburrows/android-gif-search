import org.gradle.api.JavaVersion.VERSION_11
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML
import java.io.FileInputStream
import java.nio.file.Paths
import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  kotlin("android")
  kotlin("plugin.compose")
  alias(libs.plugins.ksp)
  alias(libs.plugins.dagger)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.license)
  alias(libs.plugins.dexcount)
  alias(libs.plugins.publish)
}

android {
  namespace = "com.burrowsapps.gif.search"
  testNamespace = "com.burrowsapps.gif.search.test"
  compileSdk = libs.versions.sdk.get().toInt()

  defaultConfig {
    applicationId = "com.burrowsapps.gif.search"
    versionCode = 1
    versionName = "1.0"
    minSdk = libs.versions.sdk.get().toInt()
    targetSdk = libs.versions.sdk.get().toInt()

    testApplicationId = "com.burrowsapps.gif.search.test"
    testInstrumentationRunner = "com.burrowsapps.gif.search.test.CustomTestRunner"
    testInstrumentationRunnerArguments +=
      mapOf(
        "disableAnalytics" to "true",
      )

    resourceConfigurations += setOf("en")
    vectorDrawables.useSupportLibrary = true
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = VERSION_11
    targetCompatibility = VERSION_11
  }

  buildFeatures {
    buildConfig = true
    compose = true
  }

  lint {
    abortOnError = true
    checkAllWarnings = true
    warningsAsErrors = true
    checkTestSources = true
    checkDependencies = true
    checkReleaseBuilds = false
    lintConfig = Paths.get(project.rootDir.toString(), "config", "lint", "lint.xml").toFile()
    textReport = true
    sarifReport = true
  }

  val keyPath = System.getenv("APP_KEYS_PATH")
  val hasKeyPath = !keyPath.isNullOrEmpty()
  signingConfigs {
    getByName("debug") {
      storeFile = Paths.get(project.rootDir.toString(), "config", "signing", "debug.keystore").toFile()
      storePassword = libs.versions.password.get()
      keyAlias = libs.versions.alias.get()
      keyPassword = libs.versions.password.get()
    }

    if (hasKeyPath) {
      val keystorePropertiesFile = Paths.get(keyPath, "android-gif-search", "keystore.properties").toFile()
      val keystoreProperties = Properties()
      keystoreProperties.load(FileInputStream(keystorePropertiesFile))

      play {
        serviceAccountCredentials.set(file(keystoreProperties["playKey"].toString()))
      }

      register("release") {
        storeFile = file(keystoreProperties["storeFile"].toString())
        storePassword = keystoreProperties["storePassword"].toString()
        keyAlias = keystoreProperties["keyAlias"].toString()
        keyPassword = keystoreProperties["keyPassword"].toString()
      }
    }
  }

  buildTypes {
    getByName("debug") {
      applicationIdSuffix = ".debug"
      versionNameSuffix = "-dev"
      signingConfig = signingConfigs.getByName("debug")
    }

    // Apply fake signing config to release to test "assembleRelease" locally
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles +=
        listOf(
          getDefaultProguardFile("proguard-android-optimize.txt"),
          file(Paths.get(project.rootDir.toString(), "config", "proguard", "proguard-rules.txt").toFile()),
        )
      signingConfig = signingConfigs.getByName(if (hasKeyPath) "release" else "debug")
    }
  }

  // Allows "connectedDebugAndroidTest"
  testBuildType = if (rootProject.extra["release"] as Boolean) "release" else "debug"

  testOptions {
    unitTests.apply {
      isReturnDefaultValues = true
      isIncludeAndroidResources = true
    }
    animationsDisabled = true
    execution = "ANDROIDX_TEST_ORCHESTRATOR"
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

  dependenciesInfo {
    includeInApk = false
    includeInBundle = false
  }
}

ktlint {
  reporters {
    reporter(HTML)
  }
}

licenseReport {
  generateHtmlReport = true
}

hilt {
  enableAggregatingTask = true
}

ksp {
  arg("dagger.formatGeneratedSource", "disabled")
  arg("dagger.fastInit", "enabled")
  arg("dagger.experimentalDaggerErrorMessages", "enabled")
}

dependencies {
  // JDK 11 libs
  coreLibraryDesugaring(libs.android.desugar)

  // Kotlin
  implementation(platform(libs.kotlin.bom))
  implementation(libs.kotlin.stdlib)
  ktlintRuleset(libs.ktlint.compose.ruleset)

  // KotlinX
  implementation(platform(libs.kotlinx.coroutines.bom))
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  testImplementation(libs.kotlinx.coroutines.test)

  // Dagger / Dependency Injection
  implementation(libs.google.hilt.android)
  implementation(libs.androidx.hilt.compose)
  ksp(libs.google.hilt.compiler)
  kspTest(libs.google.hilt.compiler)
  kspAndroidTest(libs.google.hilt.compiler)
  compileOnly(libs.glassfish.javax.annotation)
  compileOnly(libs.javax.annotation.jsr250)
  compileOnly(libs.google.findbugs.jsr305)
  testImplementation(libs.google.hilt.testing)
  androidTestImplementation(libs.google.hilt.testing)

  // AndroidX
  implementation(libs.androidx.annotation)
  implementation(libs.androidx.activityktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.corektx)
  implementation(libs.androidx.palette)
  implementation(libs.androidx.startup)

  // AndroidX Lifecycle
  implementation(libs.androidx.lifecycle.viewmodelktx)

  // AndroidX UI
  implementation(libs.androidx.webkit)
  implementation(libs.google.material)

  // AndroidX Jetpack Compose
  implementation(platform(libs.androidx.compose.bom))
  debugImplementation(libs.androidx.compose.uitoolingpreview)
  debugImplementation(libs.androidx.compose.uimanifest)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.material)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.navigation)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.uitooling)
  implementation(libs.google.accompanist.drawablepainter)
  implementation(libs.google.accompanist.swiperefresh)
  androidTestImplementation(libs.androidx.compose.junit)

  // Glide
  implementation(libs.landscapist.animation)
  implementation(libs.landscapist.glide)
  implementation(libs.landscapist.palette)
  implementation(libs.bumptech.glide)
  implementation(libs.bumptech.glide.okhttp)
  ksp(libs.bumptech.glide.ksp)

  // OkIO
  implementation(libs.squareup.okio)

  // OkHTTP
  implementation(platform(libs.squareup.okhttp.bom))
  implementation(libs.squareup.okhttp)
  implementation(libs.squareup.okhttp.logging)
  testImplementation(libs.squareup.okhttp.mockwebserver)
  androidTestImplementation(libs.squareup.okhttp.mockwebserver)

  // Retrofit
  implementation(libs.squareup.moshi)
  implementation(libs.squareup.moshi.adapters)
  ksp(libs.squareup.moshi.kotlin)
  implementation(libs.squareup.retrofit)
  implementation(libs.squareup.retrofit.moshi)

  // Leakcanary
  debugImplementation(libs.squareup.leakcanary.startup)
  androidTestImplementation(libs.squareup.leakcanary.instrumentation)

  // Other
  implementation(libs.jakewharton.timber)

  // Unit Tests
  testImplementation(project(":test-resources"))
  testImplementation(libs.androidx.test.annotation)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.androidx.test.junit)
  testImplementation(libs.google.truth)
  testImplementation(libs.junit)
  testImplementation(libs.mockito.inline)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.robolectric)

  // Android Tests
  androidTestUtil(libs.androidx.test.orchestrator)
  androidTestImplementation(project(":test-resources"))
  androidTestImplementation(libs.androidx.test.annotation)
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.rules)
  androidTestImplementation(libs.google.truth)
  androidTestImplementation(libs.junit)
  androidTestImplementation(libs.robolectric.annotations)
}
