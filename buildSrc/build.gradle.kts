repositories {
  google()
  gradlePluginPortal()
  mavenCentral()
}

plugins {
  kotlin("jvm") version "1.5.21"
  `kotlin-dsl`
}

dependencies {
  implementation(kotlin("stdlib-jdk8", version = "1.5.21"))
}
