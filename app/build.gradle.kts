apply {
  plugin("com.novoda.android-command")
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
  compileSdk = deps.build.compileSdk

  defaultConfig {
    applicationId = "com.burrowsapps.example.gif"
    versionCode = 1
    versionName = "1.0"
    minSdk = deps.build.minSdk
    targetSdk = deps.build.targetSdk

    testApplicationId = "burrows.apps.example.gif.test"
    testInstrumentationRunner = "test.CustomTestRunner"
    testInstrumentationRunnerArguments += mapOf(
      "disableAnalytics" to "true",
      "clearPackageData" to "true"
    )

    resourceConfigurations += setOf("en")
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

  sourceSets {
    val commonTest = "src/commonTest/java"
    getByName("androidTest").java.srcDirs(commonTest)
    getByName("test").java.srcDirs(commonTest)
  }

  lint {
    textReport = true
    textOutput("stdout")
    isCheckAllWarnings = true
    isWarningsAsErrors = true
    lintConfig = file("${project.rootDir}/config/lint/lint.xml")
    isCheckReleaseBuilds = false
    isCheckTestSources = true
    isAbortOnError = false
  }

  signingConfigs {
    getByName("debug") {
      storeFile = file("${project.rootDir}/config/signing/debug.keystore")
      storePassword = deps.build.signing.pass
      keyAlias = deps.build.signing.alias
      keyPassword = deps.build.signing.pass
    }
  }

  buildTypes {
    getByName("debug") {
      applicationIdSuffix = ".debug"
      versionNameSuffix = "-dev"

      buildConfigField(
        "String",
        "BASE_URL",
        if (rootProject.extra["ci"] as Boolean) {
          "\"http://localhost:8080\""
        } else {
          "\"https://api.riffsy.com\""
        }
      )
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

dependencies {
  // Java 8+
  coreLibraryDesugaring(deps.android.desugarJdkLibs)

  // Debug only
  debugImplementation(deps.squareup.leakcanary)

  // Kotlin
  implementation(kotlin("stdlib", deps.versions.kotlin))
  implementation(kotlin("stdlib-common", deps.versions.kotlin))
  implementation(kotlin("stdlib-jdk7", deps.versions.kotlin))
  implementation(kotlin("stdlib-jdk8", deps.versions.kotlin))

  // Dagger / Dependency Injection
  implementation(deps.google.dagger.dagger)
  kapt(deps.google.dagger.compiler)
  kaptTest(deps.google.dagger.compiler)
  kaptAndroidTest(deps.google.dagger.compiler)
  compileOnly(deps.misc.javaxInject)
  compileOnly(deps.misc.jsr250)
  compileOnly(deps.misc.jsr305)
  testImplementation(deps.google.dagger.testing)
  androidTestImplementation(deps.google.dagger.testing)

  // Android X
  implementation(deps.android.activity)
  implementation(deps.android.activityktx)
  implementation(deps.android.appcompat)
  implementation(deps.android.core)
  implementation(deps.android.corektx)

  // Android X UI
  implementation(deps.android.constraintlayout)
  implementation(deps.google.material)

  // Image Loading
  implementation(deps.glide.glide)
  implementation(deps.glide.integration)
  kapt(deps.glide.compiler)

  // OkIO
  implementation(deps.squareup.okio)

  // OkHTTP
  implementation(platform(deps.squareup.okhttp.bom))
  implementation(deps.squareup.okhttp.okhttp)
  implementation(deps.squareup.okhttp.interceptor)
  testImplementation(deps.squareup.okhttp.mockwebserver)
  androidTestImplementation(deps.squareup.okhttp.mockwebserver)

  // Retrofit
  implementation(deps.squareup.moshi.moshi)
  implementation(deps.squareup.moshi.adapters)
  implementation(deps.squareup.retrofit.moshi)
  implementation(deps.squareup.retrofit.retrofit)
  implementation(deps.squareup.retrofit.rxjava2)
  kapt(deps.squareup.moshi.compiler)

  implementation(deps.rxjava.rxandroid)
  implementation(deps.rxjava.rxjava)

  testImplementation(deps.android.test.core)
  testImplementation(deps.android.test.junit)
  testImplementation(deps.google.truth)
  testImplementation(deps.test.junit)
  testImplementation(deps.test.mockito.inline)
  testImplementation(deps.test.mockito.kotlin)
  testImplementation(deps.test.reflections)
  testImplementation(deps.test.robolectric)

  androidTestUtil(deps.android.test.orchestrator)

  debugImplementation(deps.android.test.core)
  androidTestImplementation(deps.android.test.core)
  androidTestImplementation(deps.android.test.espresso.contrib) {
    exclude("org.checkerframework")
  }
  androidTestImplementation(deps.android.test.espresso.core)
  androidTestImplementation(deps.android.test.espresso.intents)
  androidTestImplementation(deps.android.test.junit)
  androidTestImplementation(deps.android.test.runner)
  androidTestImplementation(deps.google.truth)
  androidTestImplementation(deps.test.junit)
}
