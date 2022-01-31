plugins {
  kotlin("jvm") version "1.6.10"
}

dependencies {
  implementation(platform(kotlin("bom", deps.versions.kotlin)))
  implementation(kotlin("stdlib-jdk8"))
  implementation(deps.squareup.okhttp.mockwebserver)
}
