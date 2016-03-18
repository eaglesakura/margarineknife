#! /bin/sh
rm local.properties

./gradlew dependencies > dependencies.txt
./gradlew clean build javadoc uploadArchives uploadJavadoc
