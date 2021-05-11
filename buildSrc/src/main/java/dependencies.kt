@file:Suppress("ClassName", "SpellCheckingInspection")

import org.gradle.api.JavaVersion

object deps {
  object versions {
    const val androidGradle = "4.2.0"
    const val kotlin = "1.5.0"
    const val dagger = "2.35.1"
    const val okHttp = "4.9.1"
    const val moshi = "1.12.0"
    const val retrofit = "2.9.0"
    const val espresso = "3.3.0"
    const val glide = "4.12.0"
    val java = JavaVersion.VERSION_1_8
  }

  object build {
    const val minSdk = 23
    const val targetSdk = 30
    const val compileSdk = 30

    object signing {
      const val alias = "androiddebugkey"
      const val pass = "android"
    }
  }

  object plugin {
    const val gradle = "com.android.tools.build:gradle:${deps.versions.androidGradle}"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${deps.versions.kotlin}"
    const val command = "com.novoda:gradle-android-command-plugin:2.1.0"
    const val dexcount = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:2.1.0-RC01"
    const val apksize = "com.vanniktech:gradle-android-apk-size-plugin:0.4.0"
    const val versions = "com.github.ben-manes:gradle-versions-plugin:0.38.0"
    const val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:10.0.0"
    const val dagger = "com.google.dagger:hilt-android-gradle-plugin:${deps.versions.dagger}"
  }

  object kotlin {
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${versions.kotlin}"
  }

  object android {
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.4"
    const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:1.1.1"

    object test {
      const val core = "androidx.test:core:1.3.0"
      const val junit = "androidx.test.ext:junit:1.1.2"
      const val runner = "androidx.test:runner:1.3.0"
      const val orchestrator = "androidx.test:orchestrator:1.3.0"

      object espresso {
        const val core = "androidx.test.espresso:espresso-core:${versions.espresso}"
        const val intents = "androidx.test.espresso:espresso-intents:${versions.espresso}"
        const val contrib = "androidx.test.espresso:espresso-contrib:${versions.espresso}"
      }
    }
  }

  object rxjava {
    const val rxandroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
    const val rxjava = "io.reactivex.rxjava2:rxjava:2.2.21"
  }

  object squareup {
    const val okio = "com.squareup.okio:okio:2.10.0"
    const val leakcanary = "com.squareup.leakcanary:leakcanary-android:2.7"

    object okhttp {
      const val okhttp = "com.squareup.okhttp3:okhttp:${versions.okHttp}"
      const val interceptor = "com.squareup.okhttp3:logging-interceptor:${versions.okHttp}"
      const val mockwebserver = "com.squareup.okhttp3:mockwebserver:${versions.okHttp}"
    }

    object retrofit {
      const val retrofit = "com.squareup.retrofit2:retrofit:${versions.retrofit}"
      const val moshi = "com.squareup.retrofit2:converter-moshi:${versions.retrofit}"
      const val rxjava2 = "com.squareup.retrofit2:adapter-rxjava2:${versions.retrofit}"
    }

    object moshi {
      const val moshi = "com.squareup.moshi:moshi:${versions.moshi}"
      const val adapters = "com.squareup.moshi:moshi-adapters:${versions.moshi}"
      const val compiler = "com.squareup.moshi:moshi-kotlin-codegen:${versions.moshi}"
    }
  }

  object google {
    const val material = "com.google.android.material:material:1.2.1"
    const val truth = "com.google.truth:truth:1.1.2"

    object dagger {
      const val dagger = "com.google.dagger:hilt-android:${versions.dagger}"
      const val compiler = "com.google.dagger:hilt-android-compiler:${versions.dagger}"
    }
  }

  object glide {
    const val glide = "com.github.bumptech.glide:glide:${versions.glide}"
    const val compiler = "com.github.bumptech.glide:compiler:${versions.glide}"
    const val integration = "com.github.bumptech.glide:okhttp3-integration:${versions.glide}@aar"
  }

  object test {
    const val junit = "junit:junit:4.13.2"
    const val robolectric = "org.robolectric:robolectric:4.5"
    const val reflections = "org.reflections:reflections:0.9.11" // 0.9.12+, Scanner SubTypesScanner was not configured

    object mockito {
      const val inline = "org.mockito:mockito-inline:3.8.0"
      const val kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0"
    }
  }

  object misc {
    const val javaxInject = "org.glassfish:javax.annotation:10.0-b28"
    const val jsr250 = "javax.annotation:jsr250-api:1.0"
    const val jsr305 = "com.google.code.findbugs:jsr305:3.0.2"
  }
}
