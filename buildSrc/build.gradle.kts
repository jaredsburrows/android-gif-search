repositories {
  gradlePluginPortal()
}

plugins {
  kotlin("jvm") version "1.4.10"
  `kotlin-dsl`
}

kotlinDslPluginOptions {
  experimentalWarning.set(false)
}
