import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel
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
    classpath(deps.plugin.dagger)
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

tasks.named("dependencyUpdates", DependencyUpdatesTask::class.java).configure {
  fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
  }

  rejectVersionIf {
    isNonStable(candidate.version)
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    allWarningsAsErrors = true
    jvmTarget = deps.versions.java.toString()
  }
}
