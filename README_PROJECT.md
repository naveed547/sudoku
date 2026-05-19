# Sudoku CLI (Java) — Logic & Architecture

## Overview
This project implements a **Sudoku command-line game** with responsibilities separated so everything stays easy to test and evolve:

- **Board** model: stores the current grid and which cells are **prefilled** (fixed).
- **SudokuGenerator**: creates a valid full Sudoku solution, then removes cells to form a playable puzzle.
- **SudokuUtils**: parsing + Sudoku rules validation, completion checks, and solution counting.
- **Command framework** (`CommandFactory` + concrete `Command`s): interprets player input (`<cell> <number>`, `hint`, `check`, `clear`, `help`, `quit`).
- **BoardRenderer**: isolates all terminal rendering/formatting from core game logic.
- **GameService**: orchestrates puzzle generation and wiring so `SudokuGame` remains thin.
- **SudokuGame**: interactive loop that ties everything together.

## Recent changes
- Standardised build/test/run where possible (Maven + shaded jar).
- Fixed `HintCommand` to use the provided `Random` for deterministic unit testing.

---

## Key concepts

### Board state
- `0` means empty.
- `prefilled[r][c] == true` means the player cannot modify that cell.

Conceptually:
```java
int[][] board = new int[9][9];               // 0 = empty
boolean[][] prefilled = new boolean[9][9]; // fixed cells
```

### Where rules live
Most Sudoku logic is centralized in `SudokuUtils`:
- parse cell coordinates like `A3`,
- validate duplicates (row/column/3x3),
- check completion/validity,
- count solutions for uniqueness.

---

## How commands interact with `Board`

- **PlaceCommand** (`<cell> <number>`):
  - rejects if the target cell is prefilled,
  - otherwise sets the value at that coordinate.

- **ClearCommand** (`clear <cell>`):
  - rejects clearing prefilled cells (and typically rejects if the cell is already empty),
  - otherwise sets the cell value to `0`.

- **HintCommand** (`hint`):
  - finds an empty non-prefilled cell,
  - fills it using the generated **solution** value.

- **CheckCommand** (`check`):
  - scans the board for duplicates and prints either success or a list of problems.

---

## Architecture components

### Board (state holder)
`Board` is intended to be a “dumb state object”:
- read/write (`get`, `set`, `clear`),
- access fixed-cell flags (`isPrefilled`, etc.),
- provide derived helpers like `getEmptyNonPrefilledCells()`,
- deep-copy helper (`toArrayCopy()`) for safe validation.

### BoardRenderer (UI)
All terminal formatting (grid printing, labels/spacing) is kept out of `Board` so unit tests can focus on game logic and not I/O.

### GameService (orchestration)
`GameService` owns the “generate puzzle + keep references wired” responsibilities, so `SudokuGame` can:
- render,
- read commands,
- route via `CommandFactory`.

---

## How to run
- `mvn test`
- or run the project via `run.bat` / `run-tests.ps1`

---

## Quick mental model
1. Generator creates a correct full solution, then removes cells to build a puzzle.
2. Board stores the current grid + fixed-cell flags.
3. Utils handles parsing and Sudoku rules.
4. Commands mutate the board.
5. Game loop renders, accepts input, routes commands, stops on `quit`.
