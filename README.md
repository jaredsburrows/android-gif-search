# Android RecyclerView Gif Example in Kotlin

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Build](https://github.com/jaredsburrows/android-gif-example/workflows/build/badge.svg)](https://github.com/jaredsburrows/android-gif-example/actions)
[![Twitter Follow](https://img.shields.io/twitter/follow/jaredsburrows.svg?style=social)](https://twitter.com/jaredsburrows)

Gif RecyclerView MVVM Grid Example using Dagger 2 Hilt, Retrofit 2, Moshi, Kotlin Coroutines, Junit,
Espresso and Robolectric tests!

<p align="center">
  <a href="https://i.imgur.com/EVjzfrW.png" target="_blank"><img src="https://i.imgur.com/EVjzfrW.png" width="250px" /></a>
  <a href="https://i.imgur.com/qujcQIz.png" target="_blank"><img src="https://i.imgur.com/qujcQIz.png" width="250px" /></a>
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

Use `-Pci` to run tests against http://localhost:8080 instead of https://g.tenor.com.

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
