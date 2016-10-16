# Swrl List

Sister app to http://swrl.co, Swrl List is an Android app where you can quickly create a list of things you **S**hould **W**atch **R**ead or **L**isten to.

# Developer Guide

This is a normal Android application built with gradle.

## Tests

All development on this project will be written following TDD practices.

The Espresso Library is used to run integration tests using the AndroidJUnitRunner. To run, simply set up Android Studio to run all tests in the module.

If you wish to use gradle directly...

For Unit tests:

    gradle test
    
For all tests including Espresso tests:

Start up an Android Emulator, see https://developer.android.com/studio/run/managing-avds.html, then run:

    ./gradlew connectedAndroidTest
    
Finally, if you want to run all the instrumented tests against AWS Device Farm, first set the following ENV variables:
 1. DEVICEFARMPROJECTNAME
 2. DEVICEFARMDEVICEPOOL
 3. DEVICEFARMACCESSKEY
 4. DEVICEFARMSECRETKEY
 
See http://docs.aws.amazon.com/devicefarm/latest/developerguide/aws-device-farm-gradle-plugin-setting-up.html for details.
 
Then run:
 
     ./gradlew devicefarmUpload
