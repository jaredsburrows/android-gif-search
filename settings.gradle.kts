rootProject.name = "android-gif-example"

plugins {
  id("com.gradle.enterprise").version("3.0")
}

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
  }
}

include(":app")
