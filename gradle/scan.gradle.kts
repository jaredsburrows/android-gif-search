apply {
  plugin("com.gradle.build-scan")
}

extensions["buildScan"].withGroovyBuilder {
  "setLicenseAgreementUrl"("https://gradle.com/terms-of-service")
  "setLicenseAgree"("yes")
}
