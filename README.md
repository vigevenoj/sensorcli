# SensorCLI

Quickstart
---

1. Install dependencies
    1. Download `huelocalsdk.jar` and `huesdkresources.jar` from http://www.developers.meethue.com/documentation/java-multi-platform-and-android-sdk
    1. Install the Hue SDK jars into your local repository:
        * mvn install:install-file -Dfile=huelocalsdk.jar -DgroupId=com.philips -DartifactId=huelocalsdk -Dversion=1.11.2 -Dpackaging=jar
        * mvn install:install-file -Dfile=huesdkresources.jar -DgroupId=com.philips -DartifactId=huesdkresources -Dversion=1.11.2 -Dpackaging=jar
1. Run `mvn clean install` to build the application
1. Run the application using `mvn exec:java -Dexec.args=/path/to/configuration/app.properties`