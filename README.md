# Giphy Example

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![TravisCI OSX Build](https://img.shields.io/travis/jaredsburrows/android-glide-giphy-example/master.svg?label=OSX%20Build)](https://travis-ci.org/jaredsburrows/android-glide-giphy-example)
[![CircleCI Linux Build](https://img.shields.io/circleci/project/jaredsburrows/android-glide-giphy-example/master.svg?label=Linux%20Build)](https://circleci.com/gh/jaredsburrows/android-glide-giphy-example)
[![AppVeyor Windows Build](https://img.shields.io/appveyor/ci/jaredsburrows/android-glide-giphy-example/master.svg?label=Windows%20Build)](https://ci.appveyor.com/project/jaredsburrows/android-glide-giphy-example/branch/master)
[![Coveralls Code Coverage](https://img.shields.io/coveralls/jaredsburrows/android-glide-giphy-example/master.svg?label=Code%20Coverage)](https://coveralls.io/github/jaredsburrows/android-glide-giphy-example?branch=master)

Giphy RecyclerView Grid Example with Junit + Espresso tests

<a href="http://i.imgur.com/NGUKKmj.png" target="_blank"><img src="http://i.imgur.com/NGUKKmjm.png" /></a>
<a href="http://i.imgur.com/HCJzijT.png" target="_blank"><img src="http://i.imgur.com/HCJzijTm.png" /></a>
<a href="http://i.imgur.com/3xMzhKH.png" target="_blank"><img src="http://i.imgur.com/3xMzhKHm.png" /></a>

## Module(s)
 
 - **burrows-apps-giphy-example** - Root module
   - **src**
     - **androidTest** - UI Tests
     - **main** - Source Code
     - **test** - Unit Tests


## Setup Build Environment
**Separate Downloads:**


 - [Download Java](https://java.com/en/download/)
 - [Download Gradle](https://gradle.org/downloads) (Run `gradlew` to utilize the project `Gradle` version)
 - [Download the Android SDK](http://developer.android.com/sdk/index.html#Other)
 - [Download Android Studio(based on Intellij)](http://developer.android.com/sdk/index.html#Other)


**Bundled Download(includes all of the above except for `Git`):**


 - [Download Android Studio(based on Intellij)](http://developer.android.com/sdk/index.html)

## Setup your `local.properties`


Make sure you have your `local.properties` file in top level project folder. For the Android-SDK manager.


**Windows:**


    sdk.dir=C\:\\Users\\<user>\\android-sdk


**Linux/Mac OSX:**


    sdk.dir=/Users/<user>/android-sdk


## Building and Running (debug)


This project builds with [Gradle](www.gradle.org) and the Android Build [tools](http://tools.android.com/tech-docs/new-build-system).


**Building:**


Assemble the `.apk`:


    $ gradlew assembleDebug


**Installing:**


Assemble and Install the `.apk`:


    $ gradlew installDebug


**Running The App:**


Assemble, Install and Run the `.apk`:


    $ gradlew runDebug


## Testing


**Running the Unit Tests (debug):**


The [Junit](http://junit.org/junit4/) tests run on the JVM, no need for emulators or real devices.


    $ gradlew testDebug
    
**Running the Instrumentation Tests:**


The [Espresso](https://developer.android.com/training/testing/ui-testing/espresso-testing.html) instrumentation tests run on the device.


    $ gradlew connectedDebugAndroidTest
    

## Reports


**Generate Lint Reports (debug):**


The [Lint](http://developer.android.com/tools/help/lint.html) plugin generates reports based off the source code.


    $ gradlew lintDebug


**Generate Jacoco Test Coverage:**


The [Jacoco](http://www.eclemma.org/jacoco/) plugin generates coverage reports based off the unit tests.


    $ gradlew testDebug jacocoDebugReport
    

## Other


**Count Dex Methods:**


Assemble and Count the Dex Methods the `.apk`:


    $ gradlew countDebugDexMethods


**Size of the APK:**


Assemble and get the Size of the `.apk`:


    $ gradlew sizeDebugApk


**Dependency Updates:**


Check for Dependency and Plugin Updates:


    $ gradlew dependencyUpdates
    
