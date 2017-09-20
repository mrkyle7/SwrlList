#!/usr/bin/env bash
set -e
./gradlew test
./gradlew connectedAndroidTest
./gradlew publishRelease