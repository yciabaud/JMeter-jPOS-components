#!/bin/sh

# mvn dependency:copy-dependencies install -DexcludeGroupIds=org.apache.jmeter

JAR_LIB=jmeter-jpos-components-1.0.0-SNAPSHOT.jar

if [ $# -eq 0 ]; then
	echo "please input jmeter root folder"
	exit;
fi

cp -Rf $1/lib $1/lib-ori
cp -Rf target/${JAR_LIB} $1/lib/ext/${JAR_LIB}
cp -Rf target/dependency/* $1/lib
