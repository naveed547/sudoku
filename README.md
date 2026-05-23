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
1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd Sudoku
   ```
   *(Note: If you downloaded the source as a ZIP file instead of cloning, run `git init` in the project root before proceeding.)*

2. **Build the executable JAR:**
   ```bash
   mvn clean package
   ```
3. **Run the game:**
   ```bash
`java -jar target/sudoku-1.0.0-shaded.jar`
   ```

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

## Git Hooks Setup (Optional)

To help maintain code quality and a clean commit history, this project provides Git hooks for `pre-commit` (runs tests) and `commit-msg` (enforces Conventional Commits).

These hooks are **not automatically installed** by Maven. If you wish to use them, follow these manual steps after cloning the repository:

1.  **Copy the hooks to your local `.git/hooks` directory:**
    ```bash
    cp git-hooks/pre-commit.sh .git/hooks/pre-commit
    cp git-hooks/commit-msg.sh .git/hooks/commit-msg
    ```
2.  **Make the hook scripts executable:**
    ```bash
    chmod +x .git/hooks/pre-commit .git/hooks/commit-msg
    ```
3.  **Configure your commit message template (optional but recommended):**
    This will pre-fill your commit message editor with the Conventional Commits template.
    ```bash
    git config commit.template .gitmessage
    ```

### Running Tests
You can always run the full test suite manually:
```bash
mvn test
```

## Project layout
```text
├── Board.java         # Board model and state
├── GameService.java   # Puzzle lifecycle management
├── SudokuGame.java    # Main entry point and game loop
├── BoardRenderer.java # CLI output logic
├── commands/          # Command pattern implementations
├── git-hooks/         # Git hook scripts (pre-commit, commit-msg)
└── utils/             # Grid generation and validation logic
```

## License
MIT
