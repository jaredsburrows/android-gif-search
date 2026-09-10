pluginManagement {
  repositories {
    exclusiveContent {
      // androidx is served ONLY by Google's Maven repo, so an androidx coordinate that
      // dl.google.com cannot serve fails instead of falling through to Maven Central.
      forRepository {
        google {
          mavenContent {
            includeGroupAndSubgroups("com.android")
            includeGroupAndSubgroups("com.google")
          }
        }
      }
      filter {
        includeGroupAndSubgroups("androidx")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

  repositories {
    exclusiveContent {
      // androidx is served ONLY by Google's Maven repo, so an androidx coordinate that
      // dl.google.com cannot serve fails instead of falling through to Maven Central.
      forRepository {
        google {
          mavenContent {
            includeGroupAndSubgroups("com.android")
            includeGroupAndSubgroups("com.google")
          }
        }
      }
      filter {
        includeGroupAndSubgroups("androidx")
      }
    }
    mavenCentral()
  }
}

plugins {
  id("com.gradle.develocity") version "4.5.1"
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
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
