@echo off
copy /Y target\jmeter-jpos-components-1.0.0-SNAPSHOT.jar \apache-jmeter-2.12\lib\ext
xcopy /Y target\dependency\* \apache-jmeter-2.12\lib