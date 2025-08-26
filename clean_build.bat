@echo off
rd /s /q app\build
rd /s /q .gradle
gradlew clean
echo Build directories cleaned.
