@echo off

set JAR_LIB=jmeter-jpos-components-1.0.0-SNAPSHOT.jar

if [%1]==[] goto usage

if %1==build goto build

if exist %1\lib-ori goto deploy

xcopy /s /y %1\lib %1\lib-ori
goto deploy

:usage
echo please input build to compile or jmeter root folder to deploy
echo warning!! be careful of space
echo example: deploy.bat build
echo example: deploy.bat \var\jmeter
goto eof

:build
mvn dependency:copy-dependencies install -DexcludeGroupIds=org.apache.jmeter
goto eof

:deploy
copy /y target\%JAR_LIB% %1\lib\ext
xcopy /y target\dependency\* %1\lib
goto eof

:eof
