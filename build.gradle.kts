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

  configurations.all {
    resolutionStrategy {
      failOnVersionConflict()

      preferProjectModules()

      eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
          useVersion(deps.versions.kotlin)
        }
      }
    }
  }
}

apply {
  plugin("com.github.ben-manes.versions")
  from(file("gradle/compile.gradle.kts"))
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    allWarningsAsErrors = true
//    jvmTarget = deps.versions.java.toString()
  }
}
