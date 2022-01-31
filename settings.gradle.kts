pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
  }

  resolutionStrategy {
    eachPlugin {
      when (requested.id.id) {
        // See https://github.com/google/dagger/issues/3170, https://github.com/google/dagger/issues/2774
        "dagger.hilt.android.plugin" ->
          useModule("com.google.dagger:hilt-android-gradle-plugin:${requested.version}")
      }
    }
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
  }
}

plugins {
  id("com.gradle.enterprise") version "3.4.1"
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}

rootProject.name = "android-gif-example"

include(":app")
include(":test-shared")
