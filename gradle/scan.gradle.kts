apply {
  plugin("com.gradle.build-scan")
}

extensions["buildScan"].withGroovyBuilder {
  "setTermsOfServiceUrl"("https://gradle.com/terms-of-service")
  "setTermsOfServiceAgree"("yes")
}
