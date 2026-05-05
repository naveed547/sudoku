# Sudoku CLI (Java) — Logic & Architecture

## Overview
This project implements a **Sudoku command-line game**. It contains:
- A **Board** model that stores grid values + which cells are fixed (prefilled)
- A **SudokuGenerator** that builds a full valid solution and removes cells to create a playable puzzle
- **SudokuUtils** for parsing user input and validating moves / whole-board duplicates
- A **command framework** (`CommandFactory` + concrete `Command`s) that interprets user commands like `A3 5`, `hint`, `check`, etc.
- **SudokuGame** as the interactive loop / entry point

---

## Key Data Structures

### Board
`src/main/java/com/example/sudoku/Board.java`

Stores:
- `int[9][9] board` — current grid values (0 means empty)
- `boolean[9][9] prefilled` — `true` if the cell is fixed and cannot be changed

Important methods:
- `get(r,c)`, `set(r,c,val)`, `clear(r,c)`
- `isPrefilled(r,c)` / `setPrefilled(r,c,v)`
- `markPrefilledFromBoard()`
  - after generating/copying solution and clearing cells, it sets `prefilled[r][c] = (board[r][c] != 0)`
- `getEmptyNonPrefilledCells()`
  - returns coordinates of cells where `!prefilled` and `value == 0`
- `toArrayCopy()`
  - used by validators to avoid mutating the real board

---

## Sudoku Generation Logic

### SudokuGenerator
`src/main/java/com/example/sudoku/utils/SudokuGenerator.java`

It has two main responsibilities:
1. **Generate a full valid solution**
2. **Turn it into a puzzle** by removing values

#### 1) generateFullSolution()
- Builds an empty `9x9` array
- Uses **backtracking**:
  - Try numbers 1..9 in shuffled order
  - Place a number if it is allowed in:
    - the row
    - the column
    - the 3x3 box
  - Recursively continue until the grid is full

This guarantees the produced grid is a valid Sudoku solution.

#### 2) createPuzzle(board, solution, prefilledCount)
- Copies the full solution into the board.
- Computes how many cells to remove:
  - `toRemove = 81 - prefilledCount`
- Randomly picks cells and sets them to 0 using `board.clear(r,c)`.
- Calls `board.markPrefilledFromBoard()` so every remaining non-zero cell is marked fixed.

> Note: This generator is simple and does not enforce “unique solution” puzzles. It just removes cells randomly.

---

## Validation & Parsing

### SudokuUtils
`src/main/java/com/example/sudoku/utils/SudokuUtils.java`

#### parseCell("A3")
- Accepts letters **A..I** for rows and **1..9** for columns
- Converts into `{rowIndex, colIndex}`
- Returns `null` for invalid inputs

This is used by command parsing like `PlaceCommand`, `ClearCommand`.

#### isValidMove(board, r, c, val)
Checks that placing `val` at `(r,c)` does not create duplicates in:
- row `r`
- column `c`
- 3x3 box containing `(r,c)`

It is used by `PlaceCommand` before updating the board.

#### validateWholeBoard(board)
Detects duplicates across the full grid and returns **human-readable problem strings**.
Used by `CheckCommand`.

#### isCompleteAndValid(board)
Returns `true` only if:
- no cell is 0 (complete)
- `validateWholeBoard(board)` returns no problems (valid)

This is checked during the game loop.

---

## Command Framework (CLI)

### The Command interface
`src/main/java/com/example/sudoku/commands/Command.java`

Commands implement:
- `boolean execute(Board board, int[][] solution, Scanner sc)`

Return value meaning:
- `true` => continue game loop
- `false` => exit game loop (used by `QuitCommand`)

### CommandFactory
`src/main/java/com/example/sudoku/commands/CommandFactory.java`

Parses an input line into one of the concrete commands:
- `quit` => `QuitCommand`
- `hint` => `HintCommand`
- `check` => `CheckCommand`
- `help` => `HelpCommand`
- `clear <cell>` => `ClearCommand`
- otherwise it tries to interpret it as:
  - `<cell> <number>` => `PlaceCommand`

### Concrete Commands

#### PlaceCommand
`src/main/java/com/example/sudoku/commands/PlaceCommand.java`
- Parse cell
- Reject if cell is prefilled
- Parse number 1..9
- Accept the move and set `board[r][c] = val`
- Rule violations are reported only when running `check` (via `SudokuUtils.validateWholeBoard`).

#### ClearCommand
- Parse cell
- Reject if prefilled
- Reject if already empty
- Otherwise: `board.clear(r,c)`

#### HintCommand
- Gets all empty non-prefilled cells: `board.getEmptyNonPrefilledCells()`
- Randomly picks one
- Fills it with the **correct solution value**: `solution[r][c]`
- Prints a hint message

#### CheckCommand
- Calls `SudokuUtils.validateWholeBoard(board.toArrayCopy())`
- Prints “No duplicates detected.” or a list of problems

#### HelpCommand / UnknownCommand / QuitCommand
- Help prints the list of supported commands
- Unknown prints an error and continues
- Quit returns `false` to stop the loop

---

## Main Game Loop

### SudokuGame
`src/main/java/com/example/sudoku/SudokuGame.java`

Flow:
1. Print welcome banner
2. Create `Board`
3. Create `SudokuGenerator` using a `Random`
4. Generate full solution and create puzzle on the board
5. Enter loop:
   - display board
   - if `SudokuUtils.isCompleteAndValid(board.toArrayCopy())`:
     - print win message and break
   - read user command
   - normalize commas into spaces
   - build a `Command` via `CommandFactory.parse(...)`
   - run `cmd.execute(...)`
   - if it returns false, break

---

## Tests Added

- `src/test/java/com/example/sudoku/SudokuGameTest.java`
  - Smoke test that ensures the app prints the welcome/prompt when input immediately is `quit\n`.

Other tests verify individual commands and board behavior.

---

## How to Run

Typical:
- `mvn test`
- run via your scripts (e.g., `run.bat` / `run-tests.ps1`), or `java -cp ...`

---

## Quick Mental Model
1. **Generator** makes a correct full solution, then removes values.
2. **Board** tracks current state + fixed cells.
3. **Utils** knows how to validate and parse.
4. **Commands** perform atomic actions.
5. **Game loop** displays, checks win condition, parses input, executes commands.

