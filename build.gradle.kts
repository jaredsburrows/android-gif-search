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

tasks.withType<KotlinCompile> {
  kotlinOptions {
    allWarningsAsErrors = true
    jvmTarget = deps.versions.java.toString()
  }
}
