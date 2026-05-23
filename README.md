# Sudoku CLI (Java + Maven)

A standard 9x9 Sudoku game built for the command line.

## How it works
- Generates valid Sudoku grids and creates puzzles with 30 fixed prefilled cells.
- Ensures every generated puzzle has a unique solution using a backtracking solver with MRV heuristics.
- Supports manual entry (e.g., `A3 5` or `A3, 5`), hints, and automated solving.
- Uses ANSI escape sequences to provide a clean, "refreshing" terminal interface.

## Prerequisites
- Java 11 or higher
- Maven 3.6+

## Getting Started
Run `mvn clean package` to build the executable jar, then launch it:
`java -jar target/sudoku-1.0.0-shaded.jar`

## Commands
| Command | Action |
| :--- | :--- |
| `A3 5` | Set value 5 at cell A3 (Commas allowed: `A3, 5`) |
| `A3 clear` | Clear user-entered value at A3 |
| `hint` | Provide a valid number for a random empty cell (Max 5) |
| `check` | List rule violations (duplicates) in the current grid |
| `restart` | Revert the board to the original puzzle state and reset hints |
| `solve` | Automatically fill the board with the correct solution |
| `help` | Show command list |
| `[ENTER]` | Refresh display (or start a new game if the current one is solved) |
| `quit` | Exit game |

## Project layout
```text
├── Board.java         # Board model and state
├── GameService.java   # Puzzle lifecycle management
├── SudokuGame.java    # Main entry point and game loop
├── BoardRenderer.java # CLI output logic
├── commands/          # Command pattern implementations
└── utils/             # Grid generation and validation logic
```

## License
MIT
