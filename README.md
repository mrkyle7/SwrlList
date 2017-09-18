[![Build Status](https://travis-ci.org/mrkyle7/SwrlList.svg?branch=master)](https://travis-ci.org/mrkyle7/SwrlList)

# Swrl List

Sister app to http://swrl.co, Swrl List is an Android app where you can quickly create a list of things you **S**hould **W**atch **R**ead or **L**isten to.

Play Store Listing (Alpha): https://play.google.com/store/apps/details?id=co.swrl.list

# Developer Guide

This is a normal Android application built with gradle.

## Tests

All development on this project will be written following TDD practices.

The Espresso Library is used to run integration tests using the AndroidJUnitRunner. To run, simply set up Android Studio to run all tests in the module.

If you wish to use gradle directly...

For Unit tests:

    ./gradlew test
    
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

## Publishing to the Play Store

 1. Set the key and store password in keystore.properties (NEVER COMMIT THIS)
 2. Bump the versionCode in build.gradle
 3. Bump the versionName in build.gradle
 4. Update the whatsnew file in the play directory for the store listing
 5. Update any screenshots etc
 6. Update the strings.xml for the whatsnew in app dialog
 7. Ensure all tests pass
 8. Run:

     ./gradlew publishRelease
