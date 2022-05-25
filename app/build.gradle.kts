import org.gradle.api.JavaVersion.VERSION_11

plugins {
  alias(libs.plugins.android.application)
  kotlin("android")
  kotlin("kapt")
  alias(libs.plugins.dagger)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.license)
  alias(libs.plugins.dexcount)
}

android {
  compileSdk = libs.versions.sdk.compile.get().toInt()

  defaultConfig {
    applicationId = "com.burrowsapps.example.gif"
    versionCode = 1
    versionName = "1.0"
    minSdk = libs.versions.sdk.min.get().toInt()
    targetSdk = libs.versions.sdk.target.get().toInt()

    testApplicationId = "com.burrowsapps.example.gif.test"
    testInstrumentationRunner = "test.CustomTestRunner" // "androidx.test.runner.AndroidJUnitRunner"
    testInstrumentationRunnerArguments += mapOf(
      "clearPackageData" to "true",
      "disableAnalytics" to "true",
    )

    resourceConfigurations += setOf("en")
    vectorDrawables.useSupportLibrary = true

    buildConfigField(
      "String",
      "BASE_URL",
      if (rootProject.extra["ci"] as Boolean) {
        "\"http://localhost:8080\""
      } else {
        "\"https://g.tenor.com\""
      }
    )
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = VERSION_11
    targetCompatibility = VERSION_11
  }

  kotlinOptions {
    jvmTarget = VERSION_11.toString()
  }

  buildFeatures {
    buildConfig = true
    viewBinding = true
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

  signingConfigs {
    getByName("debug") {
      storeFile = file("${project.rootDir}/config/signing/debug.keystore")
      storePassword = libs.versions.debug.password.get()
      keyAlias = libs.versions.debug.alias.get()
      keyPassword = libs.versions.debug.password.get()
    }
  }

  // Allows "connectedDebugAndroidTest"
  testBuildType = if (rootProject.extra["release"] as Boolean) "release" else "debug"

  buildTypes {
    getByName("debug") {
      applicationIdSuffix = ".debug"
      versionNameSuffix = "-dev"
    }

    // Apply fake signing config to release to test "assembleRelease" locally
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles += listOf(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        file("${project.rootDir}/config/proguard/proguard-rules.txt")
      )
      signingConfig = signingConfigs.getByName("debug")
    }
  }

  testOptions {
    animationsDisabled = true
    unitTests.apply {
      isReturnDefaultValues = true
      isIncludeAndroidResources = true
    }
    execution = "ANDROIDX_TEST_ORCHESTRATOR"
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

  dependenciesInfo {
    includeInApk = false
    includeInBundle = false
  }
}

kapt {
  correctErrorTypes = true
  mapDiagnosticLocations = true

  arguments {
    arg("dagger.formatGeneratedSource", "disabled")
    arg("dagger.fastInit", "enabled")
    arg("dagger.experimentalDaggerErrorMessages", "enabled")
    arg("moshi.generated", "javax.annotation.Generated")
  }
}

licenseReport {
  generateHtmlReport = true
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
  testImplementation(kotlin("test"))
  testImplementation(kotlin("test-junit"))
  testImplementation(kotlin("test-common"))

  // KotlinX
  implementation(platform(libs.kotlinx.coroutines.bom))
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.coroutines.corejvm)
  implementation(libs.kotlinx.coroutinesjdk8)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.kotlinx.coroutines.testjvm)
  androidTestImplementation(libs.kotlinx.coroutines.test)
  androidTestImplementation(libs.kotlinx.coroutines.testjvm)

  // Dagger / Dependency Injection
  implementation(libs.google.hilt.android)
  kapt(libs.google.hilt.compiler)
  kaptTest(libs.google.hilt.compiler)
  kaptAndroidTest(libs.google.hilt.compiler)
  compileOnly(libs.glassfish.javax.annotation)
  compileOnly(libs.javax.annotation.jsr250)
  compileOnly(libs.google.findbugs.jsr305)
  testImplementation(libs.google.hilt.testing)
  androidTestImplementation(libs.google.hilt.testing)

  // AndroidX
  implementation(libs.androidx.annotation)
  implementation(libs.androidx.activity)
  implementation(libs.androidx.activityktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core)
  implementation(libs.androidx.corektx)
  implementation(libs.androidx.startup)

  // AndroidX UI
  implementation(libs.androidx.cardview)
  implementation(libs.androidx.constraintlayout)
  implementation(libs.androidx.recyclerview)
  implementation(libs.androidx.swiperefreshlayout)
  implementation(libs.androidx.webkit)
  implementation(libs.google.material)

  // AndroidX Lifecycle
  implementation(libs.androidx.lifecycle.common)
  implementation(libs.androidx.lifecycle.commonjdk8)
  implementation(libs.androidx.lifecycle.extensions)
  implementation(libs.androidx.lifecycle.viewmodel)
  implementation(libs.androidx.lifecycle.viewmodelktx)
  implementation(libs.androidx.lifecycle.livedata)
  implementation(libs.androidx.lifecycle.livedatacore)
  implementation(libs.androidx.lifecycle.livedatacorektx)
  implementation(libs.androidx.lifecycle.livedataktx)
  kapt(libs.androidx.lifecycle.compiler)

  // Glide
  implementation(libs.bumptech.glide)
  implementation(libs.bumptech.glide.okhttp)
  kapt(libs.bumptech.glide.compiler)

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
  kapt(libs.squareup.moshi.kotlin)
  implementation(libs.squareup.retrofit)
  implementation(libs.squareup.retrofit.moshi)

  // Leakcanary
  debugImplementation(libs.squareup.leakcanary)
  androidTestImplementation(libs.squareup.leakcanary.instrumentation)

  // Other
  implementation(libs.jakewharton.timber)

  // Unit Tests
  testImplementation(project(":test-shared"))
  testImplementation(libs.androidx.test.annotation)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.androidx.core.testing)
  testImplementation(libs.androidx.test.junit)
  testImplementation(libs.google.truth)
  testImplementation(libs.junit)
  testImplementation(libs.mockito.inline)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.reflections)
  testImplementation(libs.robolectric)

  // Android Tests
  androidTestUtil(libs.androidx.test.orchestrator)
  debugImplementation(libs.androidx.test.core) // See https://stackoverflow.com/a/69476166/950427
  androidTestImplementation(project(":test-shared"))
  androidTestImplementation(libs.androidx.test.annotation)
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.core.testing)
  androidTestImplementation(libs.androidx.espresso.contrib) {
    exclude("org.checkerframework") // See https://github.com/android/android-test/issues/861#issuecomment-872582819
  }
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.espresso.intents)
  androidTestImplementation(libs.androidx.test.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.rules)
  androidTestImplementation(libs.google.truth)
  androidTestImplementation(libs.junit)
  androidTestImplementation(libs.robolectric.annotations)
}
