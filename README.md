# Sudoku CLI Game

Production-ready Java Maven Sudoku game with backtracking generator, validation, hints, and interactive CLI.

## Features
- Interactive puzzle solving with commands: `A3 5` (place), `C5 clear`, `hint`, `check`, `quit`
- Generates unique solvable puzzles with ~30 prefilled cells
- Full validation for rows, columns, 3x3 boxes
- Prefilled cells fixed; invalid moves prevented
- Logging with SLF4J/Logback

## Build & Run
**Windows:** Double-click `build-run.bat` (kills locks, builds, runs).  

**Or manual:**
```powershell
taskkill /f /im java.exe 2>$null ; Remove-Item -Recurse -Force target -ErrorAction SilentlyContinue ; mvn clean package
```
Builds `target/sudoku-1.0.0-shaded.jar`.

## Run the game
```bash
java -jar target/sudoku-1.0.0-shaded.jar
```

Example board display:
```
   1 2 3 | 4 5 6 | 7 8 9 
A  _ _ 8 | 1 _ _ | _ _ _  A
B  6 _ _ | _ 7 8 | _ _ _  B
C  _ _ _ | 6 _ _ | _ _ _  C
   ------+-------+------
... (Prefilled cells are fixed)
```

Commands:
- `A3 4` → Place 4 at A3 (if valid)
- `B2 clear` → Clear B2 (non-prefilled only)
- `hint` → Reveal random empty cell
- `check` → Validate current board
- `quit` → Exit

## Development
- Java 11+
- Maven 3+
- Tests: `mvn test`
- Logging config: `src/main/resources/logback.xml` (optional)

## License
MIT License - see LICENSE file.

