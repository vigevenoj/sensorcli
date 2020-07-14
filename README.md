# SensorCLI

Quickstart
---

1. Install dependencies
    1. Download `huelocalsdk.jar` and `huesdkresources.jar` from http://www.developers.meethue.com/documentation/java-multi-platform-and-android-sdk
    1. Install the Hue SDK jars into your local repository:
        * mvn install:install-file -Dfile=huelocalsdk.jar -DgroupId=com.philips -DartifactId=huelocalsdk -Dversion=1.11.2 -Dpackaging=jar
        * mvn install:install-file -Dfile=huesdkresources.jar -DgroupId=com.philips -DartifactId=huesdkresources -Dversion=1.11.2 -Dpackaging=jar
    1. Optionally download the `huelocalsdk-javadoc.jar` and `huesdkresources-javadoc.jar` from Philips and install them into the local repository as well
        * mvn install:install-file -DgroupId=com.philips -DartifactId=huelocalsdk -Dversion=1.11.2 -Dfile=huelocalsdk-javadoc.jar -Dpackaging=jar -Dclassifier=javadoc
        * mvn install:install-file -DgroupId=com.philips -DartifactId=huesdkresources -Dversion=1.11.2 -Dfile=huesdkresources-javadoc.jar -Dpackaging=jar -Dclassifier=javadoc
1. Run `mvn clean install` to build the application
1. Run the SimpleHueTester to get a username and bridge IP to add to your configuration properties
1. Run the application using `mvn exec:java -Dexec.args=/path/to/configuration/app.properties`


Configuration properties
---
 * app.broker.url
 * app.client.username
 * app.client.password
 * app.client.client_id
 * app.broker.topic
 * com.ibm.ssl.protocol
 * com.ibm.ssl.trustStore
 * com.ibm.ssl.trustStorePassword
 * hue.last_connected_ip
 * hue.username
