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

allprojects {
  repositories {
    google()
    gradlePluginPortal()
  }
}

apply {
  plugin("com.github.ben-manes.versions")
  from(file("gradle/compile.gradle.kts"))
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = "10"

    allWarningsAsErrors = true
    jvmTarget = deps.versions.java.toString()
  }
}
