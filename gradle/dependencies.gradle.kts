// common
val androidGradleVersion            = "3.0.1"
val kotlinVersion                   = "1.2.20"
val supportLibraryVersion           = "27.0.2"
val daggerVersion                   = "2.14.1"
val okHttpVersion                   = "3.9.1"
val retrofitVersion                 = "2.3.0"
val espressoVersion                 = "3.0.1"
val leakCanaryVersion               = "1.5"
val multidexVersion                 = "1.0.2"
val glideVersion                    = "4.5.0"
val jacocoVersion                   = "0.7.4.201502262128"

// android plugin
extra["minSdkVersion"]              = 19
extra["targetSdkVersion"]           = 27
extra["compileSdkVersion"]          = 27
extra["buildToolsVersion"]          = "27.0.1"
extra["javaVersion"]                = "1.8"
extra["debugKeystoreUser"]          = "androiddebugkey"
extra["debugKeystorePass"]          = "android"

// classpath
extra["gradle"]                     = "com.android.tools.build:gradle:$androidGradleVersion"
extra["kotlinGradlePlugin"]         = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
extra["kotlinAndroidExtensions"]    = "org.jetbrains.kotlin:kotlin-android-extensions:$kotlinVersion"
extra["gradleAndroidCommandPlugin"] = "com.novoda:gradle-android-command-plugin:1.7.1"
extra["playPublisher"]              = "com.github.triplet.gradle:play-publisher:1.2.0"
extra["buildScanPlugin"]            = "com.gradle:build-scan-plugin:1.11"
extra["dexcountGradlePlugin"]       = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.8.2"
extra["gradleAndroidApkSizePlugin"] = "com.vanniktech:gradle-android-apk-size-plugin:0.4.0"
extra["coverallsGradlePlugin"]      = "org.kt3k.gradle.plugin:coveralls-gradle-plugin:2.8.2"
extra["gradleVersionsPlugin"]       = "com.github.ben-manes:gradle-versions-plugin:0.17.0"
extra["gradleLicensePlugin"]        = "com.jaredsburrows:gradle-license-plugin:0.7.0"
extra["detektGradlePlugin"]         = "gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.0.0.RC4-3"

// implementation
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
extra["rxJava"]                     = "io.reactivex.rxjava2:rxjava:2.1.8"
extra["dagger"]                     = "com.google.dagger:dagger:$daggerVersion"
extra["okhttp"]                     = "com.squareup.okhttp3:okhttp:$okHttpVersion"
extra["loggingInterceptor"]         = "com.squareup.okhttp3:logging-interceptor:$okHttpVersion"
extra["retrofit"]                   = "com.squareup.retrofit2:retrofit:$retrofitVersion"
extra["converterMoshi"]             = "com.squareup.retrofit2:converter-moshi:$retrofitVersion"
extra["moshiAdapters"]              = "com.squareup.moshi:moshi-adapters:1.5.0"
extra["adapterRxjava2"]             = "com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion"
extra["glide"]                      = "com.github.bumptech.glide:glide:$glideVersion"
extra["okhttp3Integration"]         = "com.github.bumptech.glide:okhttp3-integration:$glideVersion@aar"

// debugImplementation
extra["leakcanaryAndroid"]          = "com.squareup.leakcanary:leakcanary-android:$leakCanaryVersion"

// releaseImplementation
extra["leakcanaryAndroidNoOp"]      = "com.squareup.leakcanary:leakcanary-android-no-op:$leakCanaryVersion"

// kapt
extra["daggerCompiler"]             = "com.google.dagger:dagger-compiler:$daggerVersion"
extra["glideCompiler"]              = "com.github.bumptech.glide:compiler:$glideVersion"

// androidTestImplementation
extra["espressoCore"]               = "com.android.support.test.espresso:espresso-core:$espressoVersion"
extra["espressoIntents"]            = "com.android.support.test.espresso:espresso-intents:$espressoVersion"
extra["espressoContrib"]            = "com.android.support.test.espresso:espresso-contrib:$espressoVersion"
extra["testingSupportLib"]          = "com.android.support.test:testing-support-lib:0.1"
extra["runner"]                     = "com.android.support.test:runner:1.0.1"

// androidTestUtil
extra["orchestrator"]               = "com.android.support.test:orchestrator:1.0.1"

// testImplementation
extra["junit"]                      = "junit:junit:4.12"
extra["mockitoInline"]              = "org.mockito:mockito-inline:2.13.0"
extra["mockitoKotlin"]              = "com.nhaarman:mockito-kotlin-kt1.1:1.5.0"
extra["truth"]                      = "com.google.truth:truth:0.37"
extra["equalsverifier"]             = "nl.jqno.equalsverifier:equalsverifier:2.4"
extra["mockwebserver"]              = "com.squareup.okhttp3:mockwebserver:$okHttpVersion"
extra["reflections"]                = "org.reflections:reflections:0.9.11"

// jacocoAgent/androidJacocoAgent
extra["orgJacocoAgent"]             = "org.jacoco:org.jacoco.agent:$jacocoVersion"

// jacocoAnt/androidJacocoAnt
extra["orgJacocoAnt"]               = "org.jacoco:org.jacoco.ant:$jacocoVersion"
