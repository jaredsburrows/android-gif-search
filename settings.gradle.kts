pluginManagement {
  repositories {
    google()
    gradlePluginPortal()
    mavenCentral()
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
  id("com.gradle.enterprise") version "3.8.1"
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
    publishAlways()
  }
}

rootProject.name = "android-gif-example"

include(":app")
include(":test-shared")

