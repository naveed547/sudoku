@echo off
if exist target\sudoku-1.0.0-shaded.jar (
  java -jar target\sudoku-1.0.0-shaded.jar
) else (
  echo Build first with build-run.bat
  pause
)
