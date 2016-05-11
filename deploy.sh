#!/bin/sh
if [ $# -eq 0 ]; then
	echo "please input jmeter root folder"
	exit;
fi

cp -Rf $1/lib $1/lib-ori
cp -Rf target/jmeter-jpos-components-1.0.0-SNAPSHOT.jar $1/lib/ext
cp -Rf target/dependency/* $1/lib
