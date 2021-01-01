repositories {
  gradlePluginPortal()
}

plugins {
  kotlin("jvm") version "1.4.21-2"
  `kotlin-dsl`
}

kotlinDslPluginOptions {
  experimentalWarning.set(false)
}

dependencies {
  implementation(kotlin("stdlib-jdk8", version = "1.4.21-2"))
}
