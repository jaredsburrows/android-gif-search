# Android RecyclerView Gif Example

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![TravisCI  Build](https://img.shields.io/travis/jaredsburrows/android-gif-example/master.svg)](https://travis-ci.org/jaredsburrows/android-gif-example)
[![Coveralls Code Coverage](https://img.shields.io/coveralls/jaredsburrows/android-gif-example/master.svg?label=Code%20Coverage)](https://coveralls.io/github/jaredsburrows/android-gif-example?branch=master)
[![Twitter Follow](https://img.shields.io/twitter/follow/jaredsburrows.svg?style=social)](https://twitter.com/jaredsburrows)


Riffsy RecyclerView MVP Grid Example using Kotlin, Dagger 2, Retrofit 2, RxJava 2 and Databinding with Junit and Espresso tests

<a href="http://i.imgur.com/zErC6JV.png" target="_blank"><img src="http://i.imgur.com/zErC6JV.png" width="250px" /></a>

**Build the APK:**

    $ gradlew assembleDebug

**Install the APK:**

    $ gradlew installDebug

**Run the App:**

    $ gradlew runDebug

## Testing

**Run [Junit](http://junit.org/junit4/) Unit Tests:**

    $ gradlew testDebug
    
**Run [Espresso](https://developer.android.com/training/testing/ui-testing/espresso-testing.html) Instrumentation Tests:**

    $ gradlew connectedDebugAndroidTest

## Reports

**Generate [PMD](https://pmd.github.io/) Report:**

    $ gradlew pmdDebug

**Generate [JacocoReport](http://www.eclemma.org/jacoco/) Test Coverage Report:**

    $ gradlew jacocoDebugReport

**Generate [FindBugs](http://findbugs.sourceforge.net/) Report:**

    $ gradlew findBugsDebug

**Generate [Checkstyle](http://checkstyle.sourceforge.net/) Report:**

    $ gradlew checkstyleDebug

**Generate [Lint](http://developer.android.com/tools/help/lint.html) Report:**

    $ gradlew lintDebug
