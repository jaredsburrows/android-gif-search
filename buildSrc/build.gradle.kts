repositories {
  gradlePluginPortal()
}

plugins {
  kotlin("jvm") version "1.5.0"
  `kotlin-dsl`
}

dependencies {
  implementation(kotlin("stdlib-jdk8", version = "1.5.0"))
}
