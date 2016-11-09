@echo off

set JAR_LIB=jmeter-jpos-components-1.0.0-SNAPSHOT.jar
mvn dependency:copy-dependencies install -DexcludeGroupIds=org.apache.jmeter

if [%1]==[] goto usage
xcopy /s /y %1\lib %1\lib-ori
copy /y target\%JAR_LIB% %1\lib\ext
xcopy /y target\dependency\* %1\lib
goto :eof

:usage
@echo please input jmeter root folder