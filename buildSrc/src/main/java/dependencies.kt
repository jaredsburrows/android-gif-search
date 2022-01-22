@file:Suppress("ClassName", "SpellCheckingInspection")

import org.gradle.api.JavaVersion.VERSION_11

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
    val java = VERSION_11
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
    const val annotation = "androidx.annotation:annotation:1.3.0"
    const val activity = "androidx.activity:activity:1.4.0"
    const val activityktx = "androidx.activity:activity-ktx:1.4.0"
    const val appcompat = "androidx.appcompat:appcompat:1.4.1"
    const val cardview = "androidx.cardview:cardview:1.0.0"
    const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.1.3"
    const val core = "androidx.core:core:1.7.0"
    const val corektx = "androidx.core:core-ktx:1.7.0"
    const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:1.1.5"
    const val recyclerview = "androidx.recyclerview:recyclerview:1.2.1"
    const val common = "androidx.lifecycle:lifecycle-common:2.4.0"
    const val commonjdk8 = "androidx.lifecycle:lifecycle-common-java8:2.4.0"
    const val compiler = "androidx.lifecycle:lifecycle-compiler:2.4.0"
    const val extensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
    const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel:2.4.0"
    const val viewmodelktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0"
    const val livedata = "androidx.lifecycle:lifecycle-livedata:2.4.0"
    const val livedatacore = "androidx.lifecycle:lifecycle-livedata-core:2.4.0"
    const val livedatacorektx = "androidx.lifecycle:lifecycle-livedata-ktx:2.4.0"
    const val livedataktx = "androidx.lifecycle:lifecycle-livedata-ktx:2.4.0"
    const val webkit = "androidx.webkit:webkit:1.4.0"

    object test {
      const val annotation = "androidx.test:annotation:1.0.0"
      const val coretesting = "androidx.arch.core:core-testing:2.1.0"
      const val core = "androidx.test:core:1.4.0"
      const val orchestrator = "androidx.test:orchestrator:1.4.1"
      const val runner = "androidx.test:runner:1.4.0"
      const val rules = "androidx.test:rules:1.4.0"

      object ext {
        const val junit = "androidx.test.ext:junit:1.1.3"
      }

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

  const val junit = "junit:junit:4.13.2"
  const val reflections = "org.reflections:reflections:0.10.2"

  object robolectric {
    const val robolectric = "org.robolectric:robolectric:4.7.3"
    const val annotations = "org.robolectric:annotations:4.7.3"
  }

  object mockito {
    const val inline = "org.mockito:mockito-inline:4.2.0"
    const val kotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
  }

  object misc {
    const val javaxInject = "org.glassfish:javax.annotation:10.0-b28"
    const val jsr250 = "javax.annotation:jsr250-api:1.0"
    const val jsr305 = "com.google.code.findbugs:jsr305:3.0.2"
  }
}
