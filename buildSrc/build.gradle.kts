repositories {
  google()
  gradlePluginPortal()
  mavenCentral()
}

plugins {
  kotlin("jvm") version "1.6.10"
  `kotlin-dsl`
}

dependencies {
  implementation(kotlin("stdlib-jdk8", version = "1.6.10"))
}
