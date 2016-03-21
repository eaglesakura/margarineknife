#! /bin/sh
./gradlew dependencies > dependencies.txt
./gradlew clean build javadoc uploadArchives uploadJavadoc
