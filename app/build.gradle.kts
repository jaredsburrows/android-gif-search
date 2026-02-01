@file:Suppress("UnstableApiUsage")

import org.gradle.api.JavaVersion.VERSION_21
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML
import java.io.FileInputStream
import java.nio.file.Paths
import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.ksp)
  alias(libs.plugins.dagger)
  alias(libs.plugins.license)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.dexcount)
  alias(libs.plugins.publish)
}

val sdkVersion =
  libs.versions.sdk
    .get()
    .toInt()
val jvmVersion = VERSION_21

android {
  namespace = "com.burrowsapps.gif.search"
  testNamespace = "com.burrowsapps.gif.search.test"
  compileSdk {
    version =
      release(sdkVersion) {
        minorApiLevel = 1
      }
  }

  defaultConfig {
    applicationId = "com.burrowsapps.gif.search"
    versionCode = 1
    versionName = "1.0"
    minSdk {
      version = release(sdkVersion)
    }
    targetSdk {
      version = release(sdkVersion)
    }

    testApplicationId = "com.burrowsapps.gif.search.test"
    testInstrumentationRunner = "com.burrowsapps.gif.search.test.CustomTestRunner"
    testInstrumentationRunnerArguments["disableAnalytics"] = "true"

    vectorDrawables.useSupportLibrary = true
  }

  androidResources {
    localeFilters += listOf("en")
  }

  compileOptions {
    sourceCompatibility = jvmVersion
    targetCompatibility = jvmVersion
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
    lintConfig = rootDir.resolve("config/lint/lint.xml")
    textReport = true
    sarifReport = true
  }

  val keyPath = System.getenv("APP_KEYS_PATH")
  val hasKeyPath = !keyPath.isNullOrEmpty()
  signingConfigs {
    getByName("debug") {
      storeFile = rootDir.resolve("config/signing/debug.keystore")
      storePassword = libs.versions.password.get()
      keyAlias = libs.versions.alias.get()
      keyPassword = libs.versions.password.get()
    }

    if (hasKeyPath) {
      val keystorePropertiesFile = Paths.get(keyPath).resolve("android-gif-search/keystore.properties").toFile()
      val keystoreProperties =
        Properties().apply {
          load(FileInputStream(keystorePropertiesFile))
        }

      play {
        serviceAccountCredentials.set(rootDir.resolve(keystoreProperties["playKey"].toString()))
      }

      register("release") {
        storeFile = rootDir.resolve(keystoreProperties["storeFile"].toString())
        storePassword = keystoreProperties["storePassword"].toString()
        keyAlias = keystoreProperties["keyAlias"].toString()
        keyPassword = keystoreProperties["keyPassword"].toString()
      }
    }
  }

  buildTypes {
    debug {
      applicationIdSuffix = ".debug"
      versionNameSuffix = "-dev"
      signingConfig = signingConfigs.getByName("debug")
    }

    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        rootDir.resolve("config/proguard/proguard-rules.txt"),
      )
      signingConfig = signingConfigs.getByName(if (hasKeyPath) "release" else "debug")
    }
  }

  testOptions {
    unitTests {
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

kotlin {
  compilerOptions {
    jvmTarget.set(JVM_21)
  }
}

dependencies {
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
  implementation(libs.androidx.activity)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core)
  implementation(libs.androidx.palette)
  implementation(libs.androidx.startup)

  // AndroidX Lifecycle
  implementation(libs.androidx.lifecycle.viewmodel)

  // AndroidX Paging
  implementation(libs.androidx.paging.runtime)
  implementation(libs.androidx.paging.compose)

  // AndroidX UI
  implementation(libs.androidx.webkit)
  implementation(libs.google.material)

  // AndroidX Jetpack Compose
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  testImplementation(composeBom)
  androidTestImplementation(composeBom)
  debugImplementation(libs.androidx.compose.uitoolingpreview)
  debugImplementation(libs.androidx.compose.uimanifest)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.navigation)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.uitooling)
  androidTestImplementation(libs.androidx.compose.junit)

  // Room Database
  implementation(libs.androidx.room.runtime)
  implementation(libs.androidx.room)
  implementation(libs.androidx.room.paging)
  ksp(libs.androidx.room.compiler)

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
  implementation(platform(libs.squareup.retrofit.bom))
  implementation(libs.squareup.retrofit)
  implementation(libs.squareup.retrofit.moshi)

  // Leakcanary
  debugImplementation(libs.squareup.leakcanary.startup)
  androidTestImplementation(libs.squareup.leakcanary.instrumentation)

  // Other
  implementation(libs.jakewharton.timber)

  // Unit Tests
  testImplementation(projects.testResources)
  testImplementation(libs.androidx.test.annotation)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.androidx.test.junit)
  testImplementation(libs.google.truth)
  testImplementation(libs.junit)
  testImplementation(libs.mockito)
  testImplementation(libs.robolectric)

  // Android Tests
  androidTestUtil(libs.androidx.test.orchestrator)
  androidTestImplementation(projects.testResources)
  androidTestImplementation(libs.androidx.test.annotation)
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.rules)
  androidTestImplementation(libs.google.truth)
  androidTestImplementation(libs.junit)
  androidTestImplementation(libs.robolectric.annotations)
}
