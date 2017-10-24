import org.gradle.api.tasks.wrapper.Wrapper

task<Wrapper>("wrapper") {
  description = "Generate Gradle wrapper."
  group = "build"

  gradleVersion = "4.3-rc-3"
  distributionType = Wrapper.DistributionType.ALL
}
