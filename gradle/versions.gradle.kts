// Common versions
val androidGradleVersion            = "2.3.3"
val kotlinVersion                   = "1.1.4-2"
val supportLibraryVersion           = "26.0.1"
val daggerVersion                   = "2.11"
val okHttpVersion                   = "3.8.1"
val retrofitVersion                 = "2.3.0"
val espressoVersion                 = "3.0.0"
val leakCanaryVersion               = "1.5"
val multidexVersion                 = "1.0.2"
val glideVersion                    = "4.0.0"
val mocktioVersion                  = "2.8.47"

// Android plugin
extra["minSdkVersion"]              = 19
extra["targetSdkVersion"]           = 26
extra["compileSdkVersion"]          = 26
extra["buildToolsVersion"]          = "26.0.1"
extra["sourceCompatibilityVersion"] = "1.7" // JavaVersion.VERSION_1_7
extra["targetCompatibilityVersion"] = "1.7" // JavaVersion.VERSION_1_7
extra["debugKeystoreUser"]          = "androiddebugkey"
extra["debugKeystorePass"]          = "android"

// Plugins
extra["gradle"]                     = "com.android.tools.build:gradle:$androidGradleVersion"
extra["kotlinGradlePlugin"]         = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
extra["kotlinAndroidExtensions"]    = "org.jetbrains.kotlin:kotlin-android-extensions:$kotlinVersion"
extra["gradleAndroidCommandPlugin"] = "com.novoda:gradle-android-command-plugin:1.7.1"
extra["playPublisher"]              = "com.github.triplet.gradle:play-publisher:1.2.0"
extra["buildScanPlugin"]            = "com.gradle:build-scan-plugin:1.8"
extra["dexcountGradlePlugin"]       = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.7.3"
extra["gradleAndroidApkSizePlugin"] = "com.vanniktech:gradle-android-apk-size-plugin:0.3.0"
extra["coverallsGradlePlugin"]      = "org.kt3k.gradle.plugin:coveralls-gradle-plugin:2.8.1"
extra["gradleVersionsPlugin"]       = "com.github.ben-manes:gradle-versions-plugin:0.15.0"
extra["gradleLicensePlugin"]        = "com.jaredsburrows:gradle-license-plugin:0.6.0"

// Dependencies
// compile
extra["design"]                     = "com.android.support:design:$supportLibraryVersion"
extra["appcompatv7"]                = "com.android.support:appcompat-v7:$supportLibraryVersion"
extra["supportv4"]                  = "com.android.support:support-v4:$supportLibraryVersion"
extra["recyclerviewv7"]             = "com.android.support:recyclerview-v7:$supportLibraryVersion"
extra["cardviewv7"]                 = "com.android.support:cardview-v7:$supportLibraryVersion"
extra["supportAnnotations"]         = "com.android.support:support-annotations:$supportLibraryVersion"
extra["multidex"]                   = "com.android.support:multidex:$multidexVersion"
extra["multidexInstrumentation"]    = "com.android.support:multidex-instrumentation:$multidexVersion"
extra["kotlinStdlib"]               = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
extra["rxAndroid"]                  = "io.reactivex.rxjava2:rxandroid:2.0.1"
extra["rxJava"]                     = "io.reactivex.rxjava2:rxjava:2.1.3"
extra["dagger"]                     = "com.google.dagger:dagger:$daggerVersion"
extra["okhttp"]                     = "com.squareup.okhttp3:okhttp:$okHttpVersion"
extra["loggingInterceptor"]         = "com.squareup.okhttp3:logging-interceptor:$okHttpVersion"
extra["retrofit"]                   = "com.squareup.retrofit2:retrofit:$retrofitVersion"
extra["converterMoshi"]             = "com.squareup.retrofit2:converter-moshi:$retrofitVersion"
extra["moshiAdapters"]              = "com.squareup.moshi:moshi-adapters:1.5.0"
extra["adapterRxjava2"]             = "com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion"
extra["glide"]                      = "com.github.bumptech.glide:glide:$glideVersion"
extra["okhttp3Compiler"]            = "com.github.bumptech.glide:compiler:$glideVersion"
extra["okhttp3Integration"]         = "com.github.bumptech.glide:okhttp3-integration:$glideVersion@aar"

// debugCompile
extra["leakcanaryAndroid"]          = "com.squareup.leakcanary:leakcanary-android:$leakCanaryVersion"

// releaseCompile
extra["leakcanaryAndroidNoOp"]      = "com.squareup.leakcanary:leakcanary-android-no-op:$leakCanaryVersion"

// kapt
extra["compiler"]                   = "com.android.databinding:compiler:$androidGradleVersion"
extra["daggerCompiler"]             = "com.google.dagger:dagger-compiler:$daggerVersion"

// androidTestCompile
extra["dexmakerMockito"]            = "com.linkedin.dexmaker:dexmaker-mockito:2.2.0"
extra["espressoCore"]               = "com.android.support.test.espresso:espresso-core:$espressoVersion"
extra["espressoIntents"]            = "com.android.support.test.espresso:espresso-intents:$espressoVersion"
extra["espressoContrib"]            = "com.android.support.test.espresso:espresso-contrib:$espressoVersion"
extra["testingSupportLib"]          = "com.android.support.test:testing-support-lib:0.1"
extra["runner"]                     = "com.android.support.test:runner:1.0.0"

// testCompile
extra["junit"]                      = "junit:junit:4.12"
extra["mockitoCore"]                = "org.mockito:mockito-core:$mocktioVersion"
extra["mockitoInline"]              = "org.mockito:mockito-inline:$mocktioVersion"
extra["mockitoKotlin"]              = "com.nhaarman:mockito-kotlin-kt1.1:1.5.0"
extra["assertjCore"]                = "org.assertj:assertj-core:1.7.1"
extra["equalsverifier"]             = "nl.jqno.equalsverifier:equalsverifier:2.3.2"
extra["mockwebserver"]              = "com.squareup.okhttp3:mockwebserver:$okHttpVersion"
extra["reflections"]                = "org.reflections:reflections:0.9.11"
