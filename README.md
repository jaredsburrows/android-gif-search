# Android RecyclerView Gif Example in Kotlin using the Kotlin DSL

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Build](https://github.com/jaredsburrows/android-gif-example/workflows/build/badge.svg)](https://github.com/jaredsburrows/android-gif-example/actions)
[![Twitter Follow](https://img.shields.io/twitter/follow/jaredsburrows.svg?style=social)](https://twitter.com/jaredsburrows)

Gif RecyclerView MVP Grid Example using Dagger 2 Hilt, Retrofit 2, Moshi, RxJava 2, Junit, Espresso and Robolectric tests!

<a href="https://i.imgur.com/EVjzfrW.png" target="_blank"><img src="https://i.imgur.com/EVjzfrW.png" width="250px" /></a>
<a href="https://i.imgur.com/qujcQIz.png" target="_blank"><img src="https://i.imgur.com/qujcQIz.png" width="250px" /></a>

## Build

**Build the APK:**

    $ gradlew assembleDebug

**Install the APK:**

    $ gradlew installDebug

**Run the App:**

    $ gradlew runDebug

## Test

**Run [Junit](http://junit.org/junit4/) Unit Tests:**

    $ gradlew testDebug

**Run [Espresso](https://developer.android.com/training/testing/ui-testing/espresso-testing.html) Instrumentation Tests:**

This will use https://api.riffsy.com for the server.

    $ gradlew connectedDebugAndroidTest

This will use http://localhost:8080 for the server for IDE and local testing.

    $ gradlew connectedDebugAndroidTest -Pci

## Report

**Generate [Lint](http://developer.android.com/tools/help/lint.html) Report:**

    $ gradlew lintDebug

## License

    Copyright (C) 2017 Jared Burrows

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
