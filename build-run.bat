@echo off
echo Cleaning and building Sudoku...
taskkill /f /im java.exe 2>nul
rmdir /s /q target 2>nul
mvn clean package
if %errorlevel% neq 0 (
  echo Build failed.
  pause
  exit /b %errorlevel%
)
echo Build success! Running game...
java -jar target\sudoku-1.0.0-shaded.jar
pause
