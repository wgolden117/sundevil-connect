@echo off
setlocal

echo === Building SunDevil Connect ===
call gradlew clean build shadowJar

echo === Preparing app folder ===
rmdir /S /Q app 2>nul
mkdir app

copy client\build\libs\client.jar app\client.jar
xcopy /E /I "C:\Users\Weronika Golden\Documents\Libraries\javafx-sdk-21.0.7\bin" app\

echo === Packaging installer ===
rmdir /S /Q dist 2>nul

jpackage ^
  --type exe ^
  --name SunDevilConnect ^
  --input app ^
  --main-jar client.jar ^
  --main-class ser460.sundevilconnect.client.Main ^
  --dest dist ^
  --icon sundevil.ico ^
  --win-shortcut ^
  --win-menu ^
  --win-dir-chooser ^
  --app-version 1.0 ^
  --vendor "Weronika Golden" ^
  --module-path "C:\Users\Weronika Golden\Documents\Libraries\javafx-sdk-21.0.7\lib" ^
  --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.swing,java.sql,java.logging,java.naming,java.desktop ^
  --java-options "-Djava.library.path=$APPDIR"

echo.
echo Installer created in /dist
pause