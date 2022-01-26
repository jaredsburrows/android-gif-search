plugins {
  kotlin("jvm") version "1.6.10"
}

dependencies {
  implementation(platform(libs.kotlin.bom))
  implementation(kotlin("stdlib-jdk8"))
  implementation(libs.squareup.okhttp.mockwebserver)
}
