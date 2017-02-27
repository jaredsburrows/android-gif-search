# Android RecyclerView Gif Example

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![TravisCI OSX/Linux Build](https://img.shields.io/travis/jaredsburrows/android-gif-example/master.svg?label=OSX/Linux%20Build)](https://travis-ci.org/jaredsburrows/android-gif-example)
[![AppVeyor Windows Build](https://img.shields.io/appveyor/ci/jaredsburrows/android-gif-example/master.svg?label=Windows%20Build)](https://ci.appveyor.com/project/jaredsburrows/android-gif-example/branch/master)
[![Coveralls Code Coverage](https://img.shields.io/coveralls/jaredsburrows/android-gif-example/master.svg?label=Code%20Coverage)](https://coveralls.io/github/jaredsburrows/android-gif-example?branch=master)
[![Twitter Follow](https://img.shields.io/twitter/follow/jaredsburrows.svg?style=social)](https://twitter.com/jaredsburrows)


Riffsy RecyclerView MVP Grid Example using Dagger 2, Retrofit 2, RxJava 2 and Databinding with Junit and Espresso tests

<a href="http://i.imgur.com/zErC6JV.png" target="_blank"><img src="http://i.imgur.com/zErC6JV.png" width="250px" /></a>

## Module(s)
 
 - **android-gif-example** - Root module
   - **src**
     - **androidTest** - UI Tests
     - **main** - Source Code
     - **test** - Unit Tests

## Building and Running


This project builds with [Gradle](www.gradle.org) and the Android Build [tools](http://tools.android.com/tech-docs/new-build-system).


**Build the APK:**

    $ gradlew assembleDebug

**Install the APK:**

    $ gradlew installDebug

**Run the App:**

    $ gradlew runDebug

## Testing


**Running the Unit Tests:**


The [Junit](http://junit.org/junit4/) tests run on the JVM, no need for emulators or real devices.


    $ gradlew testDebug
    
**Running the Instrumentation Tests:**


The [Espresso](https://developer.android.com/training/testing/ui-testing/espresso-testing.html) instrumentation tests run on the device.


    $ gradlew connectedDebugAndroidTest
    

## Reports


**Generate Lint Reports:**


The [Lint](http://developer.android.com/tools/help/lint.html) plugin generates reports based off the source code.


    $ gradlew lintDebug


**Generate Jacoco Test Coverage:**


The [Jacoco](http://www.eclemma.org/jacoco/) plugin generates coverage reports based off the unit tests.


    $ gradlew jacocoDebugReport
