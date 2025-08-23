@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
echo Using Java version:
java -version
echo.
echo Building project...
gradlew.bat shadowJar
pause
