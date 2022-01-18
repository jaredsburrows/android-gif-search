@file:Suppress("ClassName", "SpellCheckingInspection")

import org.gradle.api.JavaVersion

object deps {
  object versions {
    const val kotlin = "1.6.10"
    const val kotlinx = "1.6.0"
    const val dagger = "2.40.5"
    const val okHttp = "4.9.3"
    const val moshi = "1.13.0"
    const val retrofit = "2.9.0"
    const val espresso = "3.4.0"
    const val glide = "4.12.0"
    val java = JavaVersion.VERSION_11
  }

  object build {
    const val minSdk = 23
    const val targetSdk = 31
    const val compileSdk = 31

    object signing {
      const val alias = "androiddebugkey"
      const val pass = "android"
    }
  }

  object plugin {
    const val command = "com.novoda:gradle-android-command-plugin:2.1.0"
    const val dagger = "com.google.dagger:hilt-android-gradle-plugin:${versions.dagger}"
  }

  object kotlin {
    object coroutines {
      const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${versions.kotlinx}"
      const val bom = "org.jetbrains.kotlinx:kotlinx-coroutines-bom:${versions.kotlinx}"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.kotlinx}"
      const val corejvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${versions.kotlinx}"
      const val corejdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${versions.kotlinx}"
      const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${versions.kotlinx}"
      const val testjvm = "org.jetbrains.kotlinx:kotlinx-coroutines-test-jvm:${versions.kotlinx}"
    }
  }

  object android {
    const val activity = "androidx.activity:activity:1.4.0"
    const val activityktx = "androidx.activity:activity-ktx:1.4.0"
    const val appcompat = "androidx.appcompat:appcompat:1.4.1"
    const val core = "androidx.core:core:1.7.0"
    const val corektx = "androidx.core:core-ktx:1.7.0"
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.1.3"
    const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:1.1.5"

    object test {
      const val core = "androidx.test:core:1.4.0"
      const val junit = "androidx.test.ext:junit:1.1.3"
      const val runner = "androidx.test:runner:1.4.0"
      const val orchestrator = "androidx.test:orchestrator:1.4.1"

      object espresso {
        const val core = "androidx.test.espresso:espresso-core:${versions.espresso}"
        const val intents = "androidx.test.espresso:espresso-intents:${versions.espresso}"
        const val contrib = "androidx.test.espresso:espresso-contrib:${versions.espresso}"
      }
    }
  }

  object squareup {
    const val okio = "com.squareup.okio:okio:3.0.0"
    const val leakcanary = "com.squareup.leakcanary:leakcanary-android:2.8.1"

    object okhttp {
      const val bom = "com.squareup.okhttp3:okhttp-bom:${versions.okHttp}"
      const val okhttp = "com.squareup.okhttp3:okhttp:${versions.okHttp}"
      const val interceptor = "com.squareup.okhttp3:logging-interceptor:${versions.okHttp}"
      const val mockwebserver = "com.squareup.okhttp3:mockwebserver:${versions.okHttp}"
    }

    object retrofit {
      const val retrofit = "com.squareup.retrofit2:retrofit:${versions.retrofit}"
      const val moshi = "com.squareup.retrofit2:converter-moshi:${versions.retrofit}"
    }

    object moshi {
      const val moshi = "com.squareup.moshi:moshi:${versions.moshi}"
      const val adapters = "com.squareup.moshi:moshi-adapters:${versions.moshi}"
      const val compiler = "com.squareup.moshi:moshi-kotlin-codegen:${versions.moshi}"
    }
  }

  object google {
    const val material = "com.google.android.material:material:1.5.0"
    const val truth = "com.google.truth:truth:1.1.3"

    object dagger {
      const val dagger = "com.google.dagger:hilt-android:${versions.dagger}"
      const val compiler = "com.google.dagger:hilt-android-compiler:${versions.dagger}"
      const val testing = "com.google.dagger:hilt-android-testing:${versions.dagger}"
    }
  }

  object glide {
    const val glide = "com.github.bumptech.glide:glide:${versions.glide}"
    const val compiler = "com.github.bumptech.glide:compiler:${versions.glide}"
    const val integration = "com.github.bumptech.glide:okhttp3-integration:${versions.glide}@aar"
  }

  object test {
    const val junit = "junit:junit:4.13.2"
    const val robolectric = "org.robolectric:robolectric:4.7.3"
    const val reflections = "org.reflections:reflections:0.10.2"

    object mockito {
      const val inline = "org.mockito:mockito-inline:4.2.0"
      const val kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
    }
  }

  object misc {
    const val javaxInject = "org.glassfish:javax.annotation:10.0-b28"
    const val jsr250 = "javax.annotation:jsr250-api:1.0"
    const val jsr305 = "com.google.code.findbugs:jsr305:3.0.2"
  }
}
