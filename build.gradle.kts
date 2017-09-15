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
    classpath(extra["gradle"])
    classpath(extra["kotlinGradlePlugin"])
    classpath(extra["gradleAndroidCommandPlugin"])
    classpath(extra["playPublisher"])
    classpath(extra["buildScanPlugin"])
    classpath(extra["dexcountGradlePlugin"])
    classpath(extra["gradleAndroidApkSizePlugin"])
    classpath(extra["coverallsGradlePlugin"])
    classpath(extra["gradleVersionsPlugin"])
    classpath(extra["gradleLicensePlugin"])
    classpath(extra["detektGradlePlugin"])
  }
}

repositories {
  google()
  jcenter()
}

apply {
  from(rootProject.file("gradle/scan.gradle"))
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
  from(rootProject.file("gradle/compile.gradle.kts"))
  from(rootProject.file("gradle/quality.gradle"))
  from(rootProject.file("gradle/publish.gradle"))
  from(rootProject.file("gradle/wrapper.gradle.kts"))
}

android {
  compileSdkVersion(extra["compileSdkVersion"] as Int)
  buildToolsVersion(extra["buildToolsVersion"] as String)

  defaultConfig {
    applicationId = "burrows.apps.example.gif"
    versionCode = 1
    versionName = "1.0"
    minSdkVersion(if (rootProject.extra["lollipop"] as Boolean) 21 else rootProject.extra["minSdkVersion"] as Int) // Optimize build speed - build with minSdk 21 if using multidex
    targetSdkVersion(rootProject.extra["targetSdkVersion"] as Int)
    testApplicationId = "burrows.apps.example.gif.test"
    testInstrumentationRunner = "test.CustomTestRunner"
    testInstrumentationRunnerArgument("disableAnalytics", "true")
    resConfigs("en")                                                            // Optimize APK size - keep only english resource files for now
    vectorDrawables.useSupportLibrary = true                                            // Optimize APK size - use vector drawables
    multiDexEnabled = true
  }

  compileOptions {
    setSourceCompatibility(extra["javaVersion"])
    setTargetCompatibility(extra["javaVersion"])
  }

  // Optimize local build speed - disable aapt PNG crunching
  aaptOptions.cruncherEnabled = extra["ci"] as Boolean

  // Optimize ci build speed - disable dexing on ci
  dexOptions.preDexLibraries = !(extra["ci"] as Boolean)

  // Need this to help IDE recognize Kotlin
  sourceSets {
    getByName("androidTest").java.srcDirs("src/androidTest/kotlin")
    getByName("debug").java.srcDirs("src/debug/kotlin")
    getByName("main").java.srcDirs("src/main/kotlin")
    getByName("test").java.srcDirs("src/test/kotlin")
  }

  lintOptions {
    textReport = true
    textOutput("stdout")
    isCheckAllWarnings = true
    isWarningsAsErrors = true
    setLintConfig(rootProject.file("${project.rootDir}/config/lint/lint.xml"))
  }

  // Add "debug.keystore" so developers can share APKs with same signatures locally
  signingConfigs {
    getByName("debug") {
      storeFile = rootProject.file("config/signing/debug.keystore")
      storePassword = extra["debugKeystorePass"] as String
      keyAlias = extra["debugKeystoreUser"] as String
      keyPassword = extra["debugKeystorePass"] as String
    }
  }

  buildTypes {
    getByName("debug") {
      if (extra["ci"] as Boolean) isTestCoverageEnabled = true                                // https://issuetracker.google.com/issues/37019591
      applicationIdSuffix = ".debug"

      if (extra["ci"] as Boolean) {
        buildConfigField("String", "BASE_URL", "\"http://localhost:8080\"")
      } else {
        buildConfigField("String", "BASE_URL", "\"https://api.riffsy.com\"")
      }
    }

    // Apply fake signing config to release to test "assembleRelease" locally
    getByName("release") {
      isMinifyEnabled = true                                                                  // Optimize APK size - remove/optimize DEX file(s)
      isShrinkResources = true                                                                // Optimize APK size - remove unused resources
      proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))             // Optimize APK size - use optimized proguard rules
      proguardFile(rootProject.file("config/proguard/proguard-rules.txt"))
      signingConfig = signingConfigs.getByName("debug")

      buildConfigField("String", "BASE_URL", "\"https://api.riffsy.com\"")
    }
  }

  testOptions {
    animationsDisabled = true
    unitTests(delegateClosureOf<TestOptions.UnitTestOptions> {
      setReturnDefaultValues(true)
      all(KotlinClosure1<Any, Test>({
        (this as Test).also { testTask ->
          testTask.extensions
            .getByType(JacocoTaskExtension::class.java)
            .setIncludeNoLocationClasses(true)
        }
      }, this))
    })
  }

  // Optimize APK size - remove excess files in the manifest and APK
  packagingOptions {
    exclude("/META-INF/*.kotlin_module")
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
    force(extra["kotlinStdlib"])
    force(extra["supportAnnotations"])
    force(extra["multidex"])
    force(extra["multidexInstrumentation"])
    force(extra["orgJacocoAgent"])
    force(extra["orgJacocoAnt"])
  }
}

dependencies {
  implementation(extra["design"])
  implementation(extra["cardviewv7"])
  implementation(extra["kotlinStdlib"])
  implementation(extra["okhttp"])
  implementation(extra["loggingInterceptor"])
  implementation(extra["adapterRxjava2"])
  implementation(extra["converterMoshi"])
  implementation(extra["moshiAdapters"])
  implementation(extra["retrofit"])
  implementation(extra["rxAndroid"])
  implementation(extra["rxJava"])
  implementation(extra["glide"])
  implementation(extra["okhttp3Integration"])
  implementation(extra["dagger"])
  implementation(extra["multidex"])

  kapt(extra["daggerCompiler"])
  kapt(extra["glideCompiler"])

  debugImplementation(extra["leakcanaryAndroid"])
  releaseImplementation(extra["leakcanaryAndroidNoOp"])

  androidTestImplementation(extra["junit"])
  androidTestImplementation(extra["truth"])
  androidTestImplementation(extra["runner"])
  androidTestImplementation(extra["espressoCore"])
  androidTestImplementation(extra["espressoIntents"])
  androidTestImplementation(extra["espressoContrib"] as String) { exclude(group = "com.android.support") }
  androidTestImplementation(extra["mockwebserver"])

  testImplementation(extra["junit"])
  testImplementation(extra["truth"])
  testImplementation(extra["mockitoKotlin"])
  testImplementation(extra["mockitoInline"])
  testImplementation(extra["leakcanaryAndroidNoOp"])
  testImplementation(extra["mockwebserver"])
  testImplementation(extra["equalsverifier"])
  testImplementation(extra["reflections"])
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    // TODO Instrumentation run failed due to 'java.lang.IllegalAccessError'
//    jvmTarget = rootProject.extra["javaVersion"] as String
  }
}
