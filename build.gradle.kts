import com.android.build.gradle.internal.dsl.TestOptions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  rootProject.apply { from(rootProject.file("gradle/dependencies.gradle.kts")) }
  rootProject.extra["ci"] = rootProject.hasProperty("ci")
  rootProject.extra["lollipop"] = rootProject.hasProperty("lollipop")

  repositories {
    google()
    jcenter()
    maven { setUrl("https://plugins.gradle.org/m2/") }
  }

  dependencies {
    classpath(extra["gradle"] as String)
    classpath(extra["kotlinGradlePlugin"] as String)
    classpath(extra["kotlinAndroidExtensions"] as String)
    classpath(extra["gradleAndroidCommandPlugin"] as String)
    classpath(extra["playPublisher"] as String)
    classpath(extra["buildScanPlugin"] as String)
    classpath(extra["dexcountGradlePlugin"] as String)
    classpath(extra["gradleAndroidApkSizePlugin"] as String)
    classpath(extra["coverallsGradlePlugin"] as String)
    classpath(extra["gradleVersionsPlugin"] as String)
    classpath(extra["gradleLicensePlugin"] as String)
    classpath(extra["detektGradlePlugin"] as String)
  }
}

repositories {
  google()
  jcenter()
}

apply {
  from(file("gradle/scan.gradle.kts"))
  plugin("com.android.application")
  plugin("kotlin-android")
  plugin("kotlin-android-extensions")
  plugin("kotlin-kapt")
  plugin("android-command")
  plugin("com.github.triplet.play")
  plugin("com.getkeepsafe.dexcount")
  plugin("com.vanniktech.android.apk.size")
  plugin("com.github.ben-manes.versions")
  plugin("com.jaredsburrows.license")
  from(file("gradle/compile.gradle.kts"))
  from(file("gradle/quality.gradle"))
  from(file("gradle/wrapper.gradle.kts"))
}

android {
  compileSdkVersion(extra["compileSdkVersion"] as Int)
  buildToolsVersion(extra["buildToolsVersion"] as String)

  defaultConfig {
    applicationId = "burrows.apps.example.gif"
    versionCode = 1
    versionName = "1.0"
    minSdkVersion(if (extra["lollipop"] as Boolean) 21 else extra["minSdkVersion"] as Int) // Optimize build speed - build with minSdk 21 if using multidex
    targetSdkVersion(extra["targetSdkVersion"] as Int)
    testApplicationId = "burrows.apps.example.gif.test"
    testInstrumentationRunner = "test.CustomTestRunner"
    testInstrumentationRunnerArgument("disableAnalytics", "true")
    resConfigs("en")                                                                    // Optimize APK size - keep only english resource files for now
    vectorDrawables.useSupportLibrary = true                                            // Optimize APK size - use vector drawables
    multiDexEnabled = true
  }

  compileOptions {
    setSourceCompatibility(extra["javaVersion"])
    setTargetCompatibility(extra["javaVersion"])
  }

  // Optimize ci build speed - disable dexing on ci
  dexOptions.preDexLibraries = !(extra["ci"] as Boolean)

  // Need this to help IDE recognize Kotlin
  sourceSets {
    val commonTest = "src/commonTest/kotlin"
    getByName("androidTest").java.srcDirs("src/androidTest/kotlin", commonTest)
    getByName("debug").java.srcDirs("src/debug/kotlin")
    getByName("main").java.srcDirs("src/main/kotlin")
    getByName("test").java.srcDirs("src/test/kotlin", commonTest)
  }

  lintOptions {
    textReport = true
    textOutput("stdout")
    isCheckAllWarnings = true
    isWarningsAsErrors = true
    lintConfig = file("${project.rootDir}/config/lint/lint.xml")
  }

  // Add "debug.keystore" so developers can share APKs with same signatures locally
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
      if (extra["ci"] as Boolean) isTestCoverageEnabled = true                                // https://issuetracker.google.com/issues/37019591
      applicationIdSuffix = ".debug"

      buildConfigField("String", "BASE_URL", if (extra["ci"] as Boolean) "\"http://localhost:8080\"" else "\"https://api.riffsy.com\"")
    }

    // Apply fake signing config to release to test "assembleRelease" locally
    getByName("release") {
      isMinifyEnabled = true                                                                    // Optimize APK size - remove/optimize DEX file(s)
      isShrinkResources = true                                                                  // Optimize APK size - remove unused resources
      proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))               // Optimize APK size - use optimized proguard rules
      proguardFile(file("config/proguard/proguard-rules.txt"))
      signingConfig = signingConfigs.getByName("debug")

      buildConfigField("String", "BASE_URL", "\"https://api.riffsy.com\"")
    }
  }

  testOptions {
    animationsDisabled = true
    unitTests(delegateClosureOf<TestOptions.UnitTestOptions> {
      isReturnDefaultValues = true
      isIncludeAndroidResources = true
      all(KotlinClosure1<Any, Test>({
        (this as Test).also { testTask ->
          testTask.extensions
            .getByType(JacocoTaskExtension::class.java)
            .isIncludeNoLocationClasses = true
        }
      }, this))
    })
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

// Resolves dependency versions across test and production APKs, specifically, transitive
// dependencies. This is required since Espresso internally has a dependency on support-annotations.
configurations.all {
  resolutionStrategy {
    force(extra["kotlinStdlib"] as String)
    force(extra["kotlinReflect"] as String)
    force(extra["supportAnnotations"] as String)
    force(extra["multidex"] as String)
    force(extra["multidexInstrumentation"] as String)
    force(extra["orgJacocoAgent"] as String)
    force(extra["orgJacocoAnt"] as String)
  }
}

dependencies {
  implementation(extra["design"] as String)
  implementation(extra["cardviewv7"] as String)
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
  implementation(extra["multidex"] as String)

  kapt(extra["daggerCompiler"] as String)
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
  testImplementation(extra["equalsverifier"] as String)
  testImplementation(extra["reflections"] as String)
}

kapt {
  useBuildCache = true
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    // TODO Instrumentation run failed due to 'java.lang.IllegalAccessError'
//    jvmTarget = extra["javaVersion"] as String
  }
}
