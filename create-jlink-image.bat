@echo off
echo Creating JLink image...

set JAVA_HOME=C:\Users\tobse\.jdks\liberica-full-17.0.14\
set MAIN_JAR=hospital-1.0-SNAPSHOT-jar-with-dependencies.jar
set MODULE_PATH=%cd%\target\%MAIN_JAR%;%JAVA_HOME%\jmods
set JLINK_IMAGE=%cd%\target\jlink-image

set BUILD_OUTPUT=%cd%\build_output
set OUTPUT_PORTABLE=%BUILD_OUTPUT%\portable\win\
set OUTPUT_INSTALLER=%BUILD_OUTPUT%\installer\

echo Module path: %MODULE_PATH%
echo Output directory: %JLINK_IMAGE%

if exist "%JLINK_IMAGE%" rmdir /s /q "%JLINK_IMAGE%"

"%JAVA_HOME%\bin\jlink.exe" ^
  --module-path "%MODULE_PATH%" ^
  --add-modules hospital,java.base,java.desktop,java.logging ^
  --launcher hospital=hospital/hospital.Game ^
  --output "%JLINK_IMAGE%" ^
  --compress=2 ^
  --strip-debug ^
  --no-header-files ^
  --no-man-pages ^
  --exclude-files="**/keytool.exe

rmdir /S /Q %BUILD_OUTPUT%


echo JLink image created at %JLINK_IMAGE%
echo Run the application with: %JLINK_IMAGE%\bin\hospital.bat

set JAR_PATH=%cd%\target

echo Creating jpackage installer...

"%JAVA_HOME%\bin\jpackage.exe" ^
  --type exe ^
  --name "Hospital" ^
  --vendor "Tobse.eu" ^
  --about-url "https://github.com/TobseF/Hospital" ^
  --input "%JAR_PATH%" ^
  --main-jar %MAIN_JAR% ^
  --runtime-image "%JLINK_IMAGE%" ^
  --dest %OUTPUT_INSTALLER% ^
  --main-class hospital.Game ^
  --icon build/hospital.ico ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut

echo Created installer in "%OUTPUT_INSTALLER%"

echo Creating portable windows image...

"%JAVA_HOME%\bin\jpackage.exe" ^
  --type app-image ^
  --name "Hospital" ^
  --vendor "Tobse.eu" ^
  --input "%JAR_PATH%" ^
  --main-jar %MAIN_JAR% ^
  --runtime-image "%JLINK_IMAGE%" ^
  --dest %OUTPUT_PORTABLE% ^
  --main-class hospital.Game ^
  --icon build/hospital.ico

echo Created portable windows image in "%OUTPUT_PORTABLE%"