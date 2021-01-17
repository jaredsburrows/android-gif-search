apply {
  plugin("com.getkeepsafe.dexcount")
  plugin("com.vanniktech.android.apk.size")
  plugin("org.jlleitschuh.gradle.ktlint")
  plugin("dagger.hilt.android.plugin")
}

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
}

android {
  compileSdkVersion(deps.build.compileSdk)

  defaultConfig {
    applicationId = "com.burrowsapps.example.gif"
    versionCode = 1
    versionName = "1.0"
    minSdkVersion(deps.build.minSdk)
    targetSdkVersion(deps.build.targetSdk)

    testApplicationId = "burrows.apps.example.gif.test"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    testInstrumentationRunnerArguments += mapOf(
      "disableAnalytics" to "true",
      "clearPackageData" to "true"
    )

    resConfigs("en")
    vectorDrawables.useSupportLibrary = true
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = deps.versions.java
    targetCompatibility = deps.versions.java
  }

  buildFeatures {
    viewBinding = true
  }

  dexOptions.preDexLibraries = !(rootProject.extra["ci"] as Boolean)

  sourceSets {
    val commonTest = "src/commonTest/java"
    getByName("androidTest").java.srcDirs(commonTest)
    getByName("test").java.srcDirs(commonTest)
  }

  lintOptions {
    textReport = true
    textOutput("stdout")
    isCheckAllWarnings = true
    isWarningsAsErrors = true
    lintConfig = file("../config/lint/lint.xml")
    isCheckReleaseBuilds = false
    isCheckTestSources = true
  }

  signingConfigs {
    getByName("debug") {
      storeFile = file("../config/signing/debug.keystore")
      storePassword = deps.build.signing.pass
      keyAlias = deps.build.signing.alias
      keyPassword = deps.build.signing.pass
    }
  }

  buildTypes {
    getByName("debug") {
      applicationIdSuffix = ".debug"

      buildConfigField("String", "BASE_URL", if (rootProject.extra["ci"] as Boolean) "\"http://localhost:8080\"" else "\"https://api.riffsy.com\"")
    }

    // Apply fake signing config to release to test "assembleRelease" locally
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
      proguardFile(file("../config/proguard/proguard-rules.txt"))
      signingConfig = signingConfigs.getByName("debug")

      buildConfigField("String", "BASE_URL", "\"https://api.riffsy.com\"")
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

  // Optimize APK size - remove excess files in the manifest and APK
  packagingOptions {
    exclude("**/*.kotlin_module")
    exclude("**/*.version")
    exclude("**/kotlin/**")
    exclude("**/*.txt")
    exclude("**/*.xml")
    exclude("**/*.properties")
  }

  dependenciesInfo {
    includeInApk = false
    includeInBundle = false
  }
}

dependencies {
  implementation(deps.android.constraintlayout)
  implementation(deps.glide.glide)
  implementation(deps.glide.integration)
  implementation(deps.google.dagger.dagger)
  implementation(deps.google.material)
  implementation(deps.kotlin.stdlib)
  implementation(deps.rxjava.rxandroid)
  implementation(deps.rxjava.rxjava)
  implementation(deps.squareup.moshi.moshi)
  implementation(deps.squareup.moshi.adapters)
  implementation(deps.squareup.okhttp.interceptor)
  implementation(deps.squareup.okhttp.okhttp)
  implementation(deps.squareup.okio)
  implementation(deps.squareup.retrofit.moshi)
  implementation(deps.squareup.retrofit.retrofit)
  implementation(deps.squareup.retrofit.rxjava2)

  coreLibraryDesugaring(deps.android.desugarJdkLibs)

  kapt(deps.glide.compiler)
  kapt(deps.google.dagger.compiler)
  kapt(deps.squareup.moshi.compiler)

  debugImplementation(deps.squareup.leakcanary)

  androidTestImplementation(deps.android.test.core)
  androidTestImplementation(deps.android.test.espresso.contrib)
  androidTestImplementation(deps.android.test.espresso.core)
  androidTestImplementation(deps.android.test.espresso.intents)
  androidTestImplementation(deps.android.test.junit)
  androidTestImplementation(deps.android.test.runner)
  androidTestImplementation(deps.google.truth)
  androidTestImplementation(deps.squareup.okhttp.mockwebserver)
  androidTestImplementation(deps.test.junit)

  androidTestUtil(deps.android.test.orchestrator)

  testImplementation(deps.android.test.core)
  testImplementation(deps.android.test.junit)
  testImplementation(deps.google.truth)
  testImplementation(deps.squareup.okhttp.mockwebserver)
  testImplementation(deps.test.junit)
  testImplementation(deps.test.mockito.inline)
  testImplementation(deps.test.mockito.kotlin)
  testImplementation(deps.test.reflections)
  testImplementation(deps.test.robolectric)
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
