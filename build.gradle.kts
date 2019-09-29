import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  rootProject.extra["ci"] = rootProject.hasProperty("ci")

  repositories {
    google()
    gradlePluginPortal()
  }

  dependencies {
    classpath(deps.plugin.gradle)
    classpath(deps.plugin.kotlin)
    classpath(deps.plugin.command)
    classpath(deps.plugin.scan)
    classpath(deps.plugin.dexcount)
    classpath(deps.plugin.apksize)
    classpath(deps.plugin.versions)
    classpath(deps.plugin.ktlint)
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

//configurations.all {
//  resolutionStrategy {
//    // classpath
//    force(rootProject.extra["ktlint"] as String)
//
//    // implementation
//    force(rootProject.extra["okio"] as String)
//    force(rootProject.extra["moshi"] as String)
//    force(rootProject.extra["rxJava"] as String)
//    force(rootProject.extra["kotlinStdlib"] as String)
//    force(rootProject.extra["kotlinReflect"] as String)
//  }
//}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    allWarningsAsErrors = true
    jvmTarget = deps.versions.java.toString()
  }
}
