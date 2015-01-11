JMeter-jPOS-components
======================

Adds a jPOS sampler to JMeter to communicate with banking services

Installation of JMeter plugin is simple, the plugin is build separate from the actual JMeter code.
# Building JMeter Plugin
`git clone https://github.com/erlanggaelfallujah/JMeter-jPOS-components.git`

`cd ~/JMeter-jPOS-components`

`mvn dependency:copy-dependencies install -DexcludeGroupIds=org.apache.jmeter`

Note: the '-DexcludeGroupIds=org.apache.jmeter' parameter tells maven not to copy the jmeter jars into the target dependencies directory. This is necessary if you run a different version of jmeter than what this plugin compiles against as when you copy over the JMeter-jPOS-components jars (see below) you will end up with different versions of the ApacheJMeter jar in jmeter's lib directory, which really confuses the app when it tries to run. 

# Download latest JMeter and unzip
From http://jmeter.apache.org/download_jmeter.cgi
Untar /unzip to a ~/jmeter

# Copy the artifacts into the JMeter lib and lib/ext Directory’s.
`cd ~/jmeter`

`cp -Rf  ~/JMeter-jPOS-components/target/jmeter-jpos-components-1.0.0-SNAPSHOT.jar lib/ext/`

`cp -Rf ~/JMeter-jPOS-components/target/dependency/* lib/`

You are all set, You can start the JMeter UI by running 
`cd ~/jmeter; bin/jmeter`

You might also be interested in http://code.google.com/p/jmeter-plugins/ for data aggregations.

# See Mom

Packager field defines your iso packager in the program, usually using xml type. You can look this example at https://www.dropbox.com/s/oww38tt8e9xf39s/iso87binary.xml?dl=0

Data field defines your set request field iso 8583. You must set request field iso 8583 in this file, and using *.properties extension. You can look this example at 
https://www.dropbox.com/s/eztf5m2zh1p1v3y/iso.properties?dl=0
