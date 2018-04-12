buildscript {
  rootProject.apply { from(rootProject.file("gradle/dependencies.gradle.kts")) }
  rootProject.extra["ci"] = rootProject.hasProperty("ci")

  repositories {
    google()
    gradlePluginPortal()
  }

  dependencies {
    classpath(extra["gradle"] as String)
    classpath(extra["kotlinGradlePlugin"] as String)
    classpath(extra["kotlinAndroidExtensions"] as String)
    classpath(extra["gradleAndroidCommandPlugin"] as String)
    classpath(extra["buildScanPlugin"] as String)
    classpath(extra["dexcountGradlePlugin"] as String)
    classpath(extra["gradleAndroidApkSizePlugin"] as String)
    classpath(extra["gradleVersionsPlugin"] as String)
  }
}

repositories {
  google()
  gradlePluginPortal()
}

apply {
  from(file("gradle/scan.gradle.kts"))
  plugin("com.android.application")
  plugin("org.jetbrains.kotlin.android")
  plugin("org.jetbrains.kotlin.android.extensions")
  plugin("org.jetbrains.kotlin.kapt")
  plugin("com.novoda.android-command")
  plugin("com.getkeepsafe.dexcount")
  plugin("com.vanniktech.android.apk.size")
  plugin("com.github.ben-manes.versions")
  from(file("gradle/compile.gradle.kts"))
}

android {
  compileSdkVersion(extra["compileSdkVersion"] as Int)
  buildToolsVersion(extra["buildToolsVersion"] as String)

  defaultConfig {
    applicationId = "burrows.apps.example.gif"
    versionCode = 1
    versionName = "1.0"
    minSdkVersion(extra["minSdkVersion"] as Int)
    targetSdkVersion(extra["targetSdkVersion"] as Int)
    testApplicationId = "burrows.apps.example.gif.test"
    testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    testInstrumentationRunnerArgument("disableAnalytics", "true")
    resConfigs("en")
    vectorDrawables.useSupportLibrary = true
  }

  compileOptions {
    setSourceCompatibility(extra["javaVersion"])
    setTargetCompatibility(extra["javaVersion"])
  }

  dexOptions.preDexLibraries = !(extra["ci"] as Boolean)

  sourceSets {
    val commonTest = "src/commonTest/java"
    getByName("androidTest").java.srcDirs("src/androidTest/java", commonTest)
    getByName("test").java.srcDirs("src/test/java", commonTest)
  }

  lintOptions {
    textReport = true
    textOutput("stdout")
    isCheckAllWarnings = true
    isWarningsAsErrors = true
    lintConfig = file("config/lint/lint.xml")
    isCheckReleaseBuilds = false
    isCheckTestSources = true
  }

  signingConfigs {
    getByName("debug") {
      storeFile = file("config/signing/debug.keystore")
      storePassword = extra["debugKeystorePass"] as String
      keyAlias = extra["debugKeystoreUser"] as String
      keyPassword = extra["debugKeystorePass"] as String
    }
  }

  buildTypes {
    getByName("debug") {
      applicationIdSuffix = ".debug"

      buildConfigField("String", "BASE_URL", if (extra["ci"] as Boolean) "\"http://localhost:8080\"" else "\"https://api.riffsy.com\"")
    }

    // Apply fake signing config to release to test "assembleRelease" locally
    getByName("release") {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
      proguardFile(file("config/proguard/proguard-rules.txt"))
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
    setExecution("ANDROID_TEST_ORCHESTRATOR")
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
}

configurations.all {
  resolutionStrategy {
    force(extra["kotlinStdlib"] as String)
    force(extra["kotlinReflect"] as String)
    force(extra["supportAnnotations"] as String)
  }
}

dependencies {
  implementation(extra["design"] as String)
  implementation(extra["cardviewv7"] as String)
  implementation(extra["constraintLayout"] as String)
  implementation(extra["kotlinStdlib"] as String)
  implementation(extra["okhttp"] as String)
  implementation(extra["loggingInterceptor"] as String)
  implementation(extra["adapterRxjava2"] as String)
  implementation(extra["converterMoshi"] as String)
  implementation(extra["moshiAdapters"] as String)
  implementation(extra["retrofit"] as String)
  implementation(extra["rxAndroid"] as String)
  implementation(extra["rxJava"] as String)
  implementation(extra["glide"] as String)
  implementation(extra["okhttp3Integration"] as String)
  implementation(extra["dagger"] as String)
  implementation(extra["daggerAndroid"] as String)
  implementation(extra["daggerAndroidSupport"] as String)

  kapt(extra["daggerCompiler"] as String)
  kapt(extra["daggerAndroidProcessor"] as String)
  kapt(extra["glideCompiler"] as String)

  debugImplementation(extra["leakcanaryAndroid"] as String)
  releaseImplementation(extra["leakcanaryAndroidNoOp"] as String)

  androidTestImplementation(extra["junit"] as String)
  androidTestImplementation(extra["truth"] as String)
  androidTestImplementation(extra["runner"] as String)
  androidTestImplementation(extra["espressoCore"] as String)
  androidTestImplementation(extra["espressoIntents"] as String)
  androidTestImplementation(extra["espressoContrib"] as String) { exclude(group = "com.android.support") }
  androidTestImplementation(extra["mockwebserver"] as String)

  androidTestUtil(extra["orchestrator"] as String)

  testImplementation(extra["junit"] as String)
  testImplementation(extra["truth"] as String)
  testImplementation(extra["mockitoKotlin"] as String)
  testImplementation(extra["mockitoInline"] as String)
  testImplementation(extra["leakcanaryAndroidNoOp"] as String)
  testImplementation(extra["mockwebserver"] as String)
  testImplementation(extra["reflections"] as String)
}

kapt {
  useBuildCache = true
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    jvmTarget = rootProject.extra["javaVersion"] as String
    allWarningsAsErrors = true
  }
}
