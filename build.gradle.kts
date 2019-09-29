import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  rootProject.apply { from(rootProject.file("gradle/dependencies.gradle.kts")) }
  rootProject.extra["ci"] = rootProject.hasProperty("ci")

  repositories {
    google()
    gradlePluginPortal()
  }

  dependencies {
    classpath(rootProject.extra["gradle"] as String)
    classpath(rootProject.extra["kotlinGradlePlugin"] as String)
    classpath(rootProject.extra["gradleAndroidCommandPlugin"] as String)
    classpath(rootProject.extra["buildScanPlugin"] as String)
    classpath(rootProject.extra["dexcountGradlePlugin"] as String)
    classpath(rootProject.extra["gradleAndroidApkSizePlugin"] as String)
    classpath(rootProject.extra["gradleVersionsPlugin"] as String)
    classpath(rootProject.extra["ktlintGradle"] as String)
  }
}

plugins {
  id("com.gradle.build-scan") version "2.4.2"
  id("com.github.ben-manes.versions") version "0.25.0"
}

allprojects {
  repositories {
    google()
    gradlePluginPortal()
  }
}

apply {
  from(file("gradle/scan.gradle.kts"))
  from(file("gradle/compile.gradle.kts"))
}

configurations.all {
  resolutionStrategy {
    // classpath
    force(rootProject.extra["ktlint"] as String)

    // implementation
    force(rootProject.extra["okio"] as String)
    force(rootProject.extra["moshi"] as String)
    force(rootProject.extra["rxJava"] as String)
    force(rootProject.extra["kotlinStdlib"] as String)
    force(rootProject.extra["kotlinReflect"] as String)
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    allWarningsAsErrors = true
    jvmTarget = rootProject.extra["javaVersion"] as String
  }
}
