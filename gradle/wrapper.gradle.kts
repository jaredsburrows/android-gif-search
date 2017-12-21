import org.gradle.api.tasks.wrapper.Wrapper

task<Wrapper>("wrapper") {
  description = "Generate Gradle wrapper."
  group = "build"

  gradleVersion = "4.4.1"
  distributionType = Wrapper.DistributionType.ALL
}
