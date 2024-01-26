# Gif Search using Jetpack Compose + Kotlin

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![build](https://github.com/jaredsburrows/android-gif-search/actions/workflows/build.yml/badge.svg)](https://github.com/jaredsburrows/android-gif-search/actions/workflows/build.yml)
[![Twitter Follow](https://img.shields.io/twitter/follow/jaredsburrows.svg?style=social)](https://twitter.com/jaredsburrows)


Gif LazyVerticalGrid MVVM using Dagger 2 + Hilt with Retrofit 2, Moshi, Kotlin Coroutines,
JUnit, Espresso and Robolectric tests!

<p align="center">
  <a href="https://i.imgur.com/BATyXSX_d.webp?maxwidth=760&fidelity=grand" target="_blank"><img src="https://i.imgur.com/BATyXSX_d.webp?maxwidth=760&fidelity=grand" width="250px" /></a>
  <a href="https://i.imgur.com/TXiAiS2_d.webp?maxwidth=760&fidelity=grand" target="_blank"><img src="https://i.imgur.com/TXiAiS2_d.webp?maxwidth=760&fidelity=grand" width="250px" /></a>
</p>

<p align="center">
  <a href="https://play.google.com/store/apps/details?id=com.burrowsapps.gif.search"><img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="100"/>
</p>

## Build

**Build the APK:**

```shell
gradlew assembleDebug
```

**Install the APK:**

```shell
gradlew installDebug
```

## Test

**Run [Junit](https://junit.org/junit4/) Unit Tests:**

```shell
gradlew testDebug
```

**Run [Espresso](https://developer.android.com/training/testing/ui-testing/espresso-testing.html)
Instrumentation Tests:**

```shell
gradlew connectedDebugAndroidTest
```

## Report

**Generate [Dex Method Count](https://github.com/KeepSafe/dexcount-gradle-plugin) Report:**

```shell
gradlew countDebugDexMethods
```

**Generate [License](https://github.com/jaredsburrows/gradle-license-plugin) Report:**

```shell
gradlew licenseDebugReport
```

**Generate [Lint](https://developer.android.com/tools/help/lint.html) Report:**

```shell
gradlew lintDebug
```

## Local Debug Run

```shell
gradlew dependencyUpdates
```

```shell
osv-scanner -r . && \

gradlew ktlintCheck && \
gradlew ktlintFormat && \

gradlew lintDebug && \
gradlew lintFixDebug  && \

gradlew testDebug
```

## License

```
Copyright (C) 2017 Jared Burrows

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
