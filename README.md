# Sudoku CLI (Java + Maven)

Interactive 9x9 Sudoku game played in the terminal.

## How it works
- The game generates a full valid solution and then removes cells to create a puzzle.
- Prefilled cells are **fixed**.
- User moves are **accepted** (including moves that create duplicates).
- Rule violations (row/column/3x3 box duplicates, and incomplete/invalid board) are reported when you run **`check`**.


## Prerequisites
- Java 11+
- Maven 3+

## Standard build / test / run
All commands use Maven (company standard):

### Test
```bash
mvn test
```

### Build (fat/shaded jar)
```bash
mvn clean package -DskipTests=true
```

### Run
```bash
java -jar target/sudoku-1.0.0-shaded.jar
```


## Commands
```text
A3 5       Place value 5 at cell A3 (unless the cell is prefilled)
A3 clear   Clear cell A3 (only if non-prefilled)
hint       Fill one empty non-prefilled cell with its solution value
check      Scan the current grid and report rule violations
help       Print command help
quit       Exit the game
```


## Example
```text
Enter command (eg: A3 4, C5 clear, hint, check, quit):
```

## Notes on performance / determinism
- `SudokuGenerator` generates solutions using backtracking.
- The CLI has small allocation optimizations (example: `hint` reuses a single `Random`).
- The generator/puzzle creation strategy stays deterministic for the same input seed, so unit tests expecting specific puzzle states continue to pass.

## Tests
Run all unit tests:
```bash
mvn test
```

## Project layout
```text
src/main/java/com/example/sudoku/
  Board.java
  GameService.java
  SudokuGame.java
  BoardRenderer.java
  commands/
    CheckCommand.java
    ClearCommand.java
    Command.java
    CommandFactory.java
    CommandResult.java
    HelpCommand.java
    HintCommand.java
    PlaceCommand.java
    QuitCommand.java
    UnknownCommand.java
  utils/
    SudokuGenerator.java
    SudokuValidator.java

src/test/java/com/example/sudoku/
  BoardTest.java
  BoardRendererTest.java
  commands/*Test.java
  utils/*Test.java
```

## License
MIT (see `LICENSE`).

