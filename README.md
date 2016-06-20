# Giphy Example

Giphy RecyclerView Grid Example with Robolectric + Espresso tests

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


The [Robolectric](http://robolectric.org/) unit tests run on the JVM, no need for emulators or real devices.


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
    
