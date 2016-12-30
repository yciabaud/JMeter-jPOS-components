#!/bin/sh
JAR_LIB=jmeter-jpos-components-1.0.0-SNAPSHOT.jar

if [ $# -eq 0 ]; then
	echo "please input build to compile or jmeter root folder to deploy"
	echo "example: sh deploy.sh build"
	echo "example: sh deploy.sh /var/jmeter"
	exit;
fi

if [ $1 == "build" ]; then
	mvn dependency:copy-dependencies install -DexcludeGroupIds=org.apache.jmeter
	exit;
fi

if [ ! -d $1/lib-ori ]; then
	cp -Rf $1/lib $1/lib-ori
fi

cp -Rf target/${JAR_LIB} $1/lib/ext/${JAR_LIB}
cp -Rf target/dependency/* $1/lib