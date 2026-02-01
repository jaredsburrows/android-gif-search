pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
  
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id.startsWith("com.android")) {
        // Map AGP 9.0.0 to latest available version until 9.0.0 is published
        if (requested.version == "9.0.0") {
          useVersion("8.5.2")
        }
      }
    }
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

  repositories {
    google()
    mavenCentral()
  }
}

plugins {
  id("com.gradle.develocity") version ("4.3.2")
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
    val isCI = System.getenv("CI") != null
    publishing.onlyIf { isCI }
  }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "android-gif-search"

include(":app")
include(":test-resources")
