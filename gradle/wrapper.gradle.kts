task<Wrapper>("wrapper") {
  description = "Generate Gradle wrapper."
  group = "build"

  gradleVersion = "4.1-rc-1"
  distributionType = Wrapper.DistributionType.ALL
}
