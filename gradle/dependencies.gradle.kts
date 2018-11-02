// common
val androidGradleVersion            = "3.4.0-alpha01"
val kotlinVersion                   = "1.3.0"
val androidXVersion                 = "1.0.0"
val daggerVersion                   = "2.16" // https://github.com/google/dagger/issues/1245
val okHttpVersion                   = "3.11.0"
val retrofitVersion                 = "2.4.0"
val espressoVersion                 = "3.1.0"
val leakCanaryVersion               = "1.5.4" // TODO 1.6.1 crash?
val glideVersion                    = "4.8.0"

// android plugin
extra["minSdkVersion"]              = 19
extra["targetSdkVersion"]           = 28
extra["compileSdkVersion"]          = 28
extra["javaVersion"]                = "1.8"
extra["debugKeystoreUser"]          = "androiddebugkey"
extra["debugKeystorePass"]          = "android"

// classpath
extra["gradle"]                     = "com.android.tools.build:gradle:$androidGradleVersion"
extra["kotlinGradlePlugin"]         = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
extra["kotlinAndroidExtensions"]    = "org.jetbrains.kotlin:kotlin-android-extensions:$kotlinVersion"
extra["gradleAndroidCommandPlugin"] = "com.novoda:gradle-android-command-plugin:2.0.1"
extra["buildScanPlugin"]            = "com.gradle:build-scan-plugin:1.16"
extra["dexcountGradlePlugin"]       = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.8.4"
extra["gradleAndroidApkSizePlugin"] = "com.vanniktech:gradle-android-apk-size-plugin:0.4.0"
extra["gradleVersionsPlugin"]       = "com.github.ben-manes:gradle-versions-plugin:0.20.0"
extra["ktlintGradle"]               = "gradle.plugin.org.jlleitschuh.gradle:ktlint-gradle:5.1.0"

// implementation
extra["material"]                   = "com.google.android.material:material:1.0.0"
extra["constraintLayout"]           = "androidx.constraintlayout:constraintlayout:1.1.3"
extra["kotlinStdlib"]               = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion"
extra["kotlinReflect"]              = "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion"
extra["rxAndroid"]                  = "io.reactivex.rxjava2:rxandroid:2.1.0"
extra["rxJava"]                     = "io.reactivex.rxjava2:rxjava:2.2.3"
extra["dagger"]                     = "com.google.dagger:dagger:$daggerVersion"
extra["daggerAndroid"]              = "com.google.dagger:dagger-android:$daggerVersion"
extra["daggerAndroidSupport"]       = "com.google.dagger:dagger-android-support:$daggerVersion"
extra["okhttp"]                     = "com.squareup.okhttp3:okhttp:$okHttpVersion"
extra["loggingInterceptor"]         = "com.squareup.okhttp3:logging-interceptor:$okHttpVersion"
extra["retrofit"]                   = "com.squareup.retrofit2:retrofit:$retrofitVersion"
extra["converterMoshi"]             = "com.squareup.retrofit2:converter-moshi:$retrofitVersion"
extra["moshiAdapters"]              = "com.squareup.moshi:moshi-adapters:1.7.0"
extra["adapterRxjava2"]             = "com.squareup.retrofit2:adapter-rxjava2:$retrofitVersion"
extra["glide"]                      = "com.github.bumptech.glide:glide:$glideVersion"
extra["okhttp3Integration"]         = "com.github.bumptech.glide:okhttp3-integration:$glideVersion@aar"

// debugImplementation
extra["leakcanaryAndroid"]          = "com.squareup.leakcanary:leakcanary-android:$leakCanaryVersion"

// releaseImplementation
extra["leakcanaryAndroidNoOp"]      = "com.squareup.leakcanary:leakcanary-android-no-op:$leakCanaryVersion"

// kapt
extra["daggerCompiler"]             = "com.google.dagger:dagger-compiler:$daggerVersion"
extra["daggerAndroidProcessor"]     = "com.google.dagger:dagger-android-processor:$daggerVersion"
extra["glideCompiler"]              = "com.github.bumptech.glide:compiler:$glideVersion"

// androidTestImplementation
extra["espressoCore"]               = "androidx.test.espresso:espresso-core:$espressoVersion"
extra["espressoIntents"]            = "androidx.test.espresso:espresso-intents:$espressoVersion"
extra["espressoContrib"]            = "androidx.test.espresso:espresso-contrib:$espressoVersion"
extra["runner"]                     = "androidx.test:runner:1.1.0"

// androidTestUtil
extra["orchestrator"]               = "androidx.test:orchestrator:1.1.0"

// testImplementation
extra["junit"]                      = "junit:junit:4.12"
extra["mockitoInline"]              = "org.mockito:mockito-inline:2.23.0"
extra["mockitoKotlin"]              = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.0.0"
extra["truth"]                      = "com.google.truth:truth:0.42"
extra["mockwebserver"]              = "com.squareup.okhttp3:mockwebserver:$okHttpVersion"
extra["reflections"]                = "org.reflections:reflections:0.9.11"
