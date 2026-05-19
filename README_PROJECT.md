# Sudoku CLI (Java) — Project Architecture

## Table of Contents
1. [Project Overview](#project-overview)
2. [Entry Point: SudokuGame](#entry-point-sudokugame)
3. [Board Model](#board-model)
4. [BoardRenderer (UI)](#boardrenderer-ui)
5. [GameService](#gameservice)
6. [SudokuGenerator](#sudokugenerator)
7. [SudokuValidator](#sudokuvalidator)
8. [Command Framework](#command-framework)
9. [How to Run](#how-to-run)

---

## Project Overview

This is a command-line Sudoku game written in Java. The architecture separates concerns:

| Layer | Purpose |
|-------|---------|
| `Board` | Stores grid state and prefilled flags (dumb data holder) |
| `SudokuGenerator` | Generates valid puzzles via backtracking |
| `SudokuValidator` | Static validation utilities (parsing, rule checking, solution counting) |
| `CommandFactory` + `Command` implementations | Interprets user input into actions |
| `BoardRenderer` | All terminal formatting/I/O |
| `GameService` | Wires puzzle generation together |
| `SudokuGame` | Main loop: render → read → parse → execute |

---

## Entry Point: SudokuGame

**File:** `src/main/java/com/example/sudoku/SudokuGame.java`

```java
// Line 17: Static entry point — creates instance and starts the game
public static void main(String[] args) {
    new SudokuGame().start();
}
```

```java
// Lines 21–60: Main game loop
private void start() {
    // Line 23: Create empty board
    Board board = new Board();

    // Line 24: Create renderer for terminal output
    BoardRenderer renderer = new BoardRenderer();

    // Line 25: Wire up puzzle generator with fresh Random seed
    GameService gameService = new GameService(new SudokuGenerator(new java.util.Random()));

    // Line 26: Print welcome banner
    renderer.printWelcome();

    // Line 28: Generate first puzzle, store solution for hints/validation
    int[][] solution = gameService.newPuzzle(board);

    // Line 30: Scanner auto-closes at end of try block
    try (Scanner sc = new Scanner(System.in)) {
        while (true) {
            // Line 32: Render current board state; puzzleStarted flag changes header text
            renderer.render(board, board.isPuzzleStarted());

            // Line 34: Check if puzzle is complete and valid
            if (SudokuValidator.isCompleteAndValid(board.toArrayCopy())) {
                // Line 35: Show success message
                renderer.printCompletionSuccess();
                // Line 36: Wait for Enter to start new puzzle
                sc.nextLine();

                // Lines 38–40: Reset everything for fresh puzzle
                board = new Board();
                gameService = new GameService(new SudokuGenerator(new java.util.Random()));
                solution = gameService.newPuzzle(board);
                continue;
            }

            // Line 44: Read player input
            String line = sc.nextLine().trim();
            // Line 45–47: Skip empty lines
            if (line.isEmpty()) {
                continue;
            }

            // Line 48: Allow comma as separator (e.g., "A3, 5" → "A3 5")
            line = line.replace(",", " ").trim();

            // Line 49: Parse input string into a Command object
            Command cmd = CommandFactory.parse(line, board, solution, sc);

            // Line 50: Execute command, get result
            CommandResult result = cmd.execute(board, solution, sc);

            // Lines 52–54: Print any message from command
            if (result != null && result.message != null) {
                System.out.print(result.message);
            }

            // Lines 55–57: quit command returns success=false, breaking the loop
            if (result != null && !result.success) {
                break;
            }
        }
    }
}
```

---

## Board Model

**File:** `src/main/java/com/example/sudoku/Board.java`

```java
// Line 10: Standard Sudoku is 9×9
public static final int SIZE = 9;

// Line 11: How many cells remain prefilled in generated puzzles (30 default)
public static final int PREFILLED_COUNT = 30;

// Line 13: 2D array storing cell values (0 = empty)
private final int[][] board = new int[SIZE][SIZE];

// Line 14: Parallel array marking which cells are fixed/prefilled
private final boolean[][] prefilled = new boolean[SIZE][SIZE];

// Line 16: Tracks if player has made any move (changes header from "puzzle" to "grid")
private boolean puzzleStarted = false;

// Lines 19–21: Copy solution values into board (used during puzzle generation)
public void copyFromSolution(int[][] solution) {
    for (int i = 0; i < SIZE; i++) System.arraycopy(solution[i], 0, board[i], 0, SIZE);
}

// Lines 27–34: Reset all state for new puzzle
public void resetState() {
    for (int r = 0; r < SIZE; r++) {
        Arrays.fill(board[r], 0);
        Arrays.fill(prefilled[r], false);
    }
    puzzleStarted = false;
}

// Lines 36–42: puzzleStarted getter/setter
public boolean isPuzzleStarted() { return puzzleStarted; }
public void setPuzzleStarted(boolean v) { this.puzzleStarted = v; }

// Lines 44–48: Basic cell accessors
public int get(int r, int c) { return board[r][c]; }
public void set(int r, int c, int val) { board[r][c] = val; }
public void clear(int r, int c) { board[r][c] = 0; }
public boolean isPrefilled(int r, int c) { return prefilled[r][c]; }
public void setPrefilled(int r, int c, boolean v) { prefilled[r][c] = v; }

// Lines 50–56: Return list of cells that are empty AND not prefilled (hint targets)
public List<int[]> getEmptyNonPrefilledCells() {
    List<int[]> empties = new ArrayList<>();
    for (int r = 0; r < SIZE; r++)
        for (int c = 0; c < SIZE; c++)
            if (!prefilled[r][c] && board[r][c] == 0) empties.add(new int[]{r, c});
    return empties;
}

// Lines 58–62: Deep copy for validation (prevents mutation during checks)
public int[][] toArrayCopy() {
    int[][] out = new int[SIZE][SIZE];
    for (int i = 0; i < SIZE; i++) System.arraycopy(board[i], 0, out[i], 0, SIZE);
    return out;
}

// Lines 64–68: Mark prefilled cells based on non-zero values (called after puzzle creation)
public void markPrefilledFromBoard() {
    for (int r = 0; r < SIZE; r++)
        for (int c = 0; c < SIZE; c++)
            prefilled[r][c] = board[r][c] != 0;
}
```

---

## BoardRenderer (UI)

**File:** `src/main/java/com/example/sudoku/BoardRenderer.java`

```java
// Lines 5–7: Welcome banner (printed once at startup)
public void printWelcome() {
    printLine("Welcome to Sudoku! (Prefilled cells are fixed and cannot be changed)\n");
}

// Lines 9–15: Success screen when puzzle is completed
public void printCompletionSuccess() {
    StringBuilder sb = new StringBuilder();
    sb.append("You have successfully completed the Sudoku puzzle!");
    sb.append(System.lineSeparator());
    sb.append("Press ENTER to play again...");
    printLine(sb.toString());
}

// Lines 22–50: Main board rendering
public void render(Board board, boolean puzzleStarted) {
    StringBuilder sb = new StringBuilder();

    // Line 25: Different header depending on game state
    sb.append(puzzleStarted ? "Current grid:" : "Here is your puzzle:").append('\n');

    // Lines 26–28: Column headers and top border
    String colsHeader = "   1 2 3 | 4 5 6 | 7 8 9 ";
    sb.append(colsHeader).append('\n');
    sb.append("   ------+-------+------- ").append('\n');

    // Lines 30–45: Draw each row with labels and box separators
    for (int r = 0; r < Board.SIZE; r++) {
        char rowLabel = (char) ('A' + r);
        sb.append(rowLabel).append("  ");

        for (int c = 0; c < Board.SIZE; c++) {
            int val = board.get(r, c);
            // Line 36: Empty cells shown as underscore
            String cell = (val == 0) ? "_" : String.valueOf(val);
            sb.append(cell).append(" ");
            // Line 38: Vertical box divider after columns 3 and 6
            if (c % 3 == 2 && c < 8) sb.append("| ");
        }

        sb.append('\n');
        // Line 42: Horizontal box divider after rows 3 and 6
        if (r % 3 == 2 && r < 8) {
            sb.append("   ------+-------+------- ").append('\n');
        }
    }

    // Lines 47–49: Command prompt
    sb.append("\nEnter command (eg: A3 4, clear C5, hint, check, quit, help): ");
    sb.append(System.lineSeparator());
    printLine(sb.toString());
}

// Line 52–54: Single output method (allows easy mocking in tests)
private void printLine(String s) {
    System.out.println(s);
}
```

---

## GameService

**File:** `src/main/java/com/example/sudoku/GameService.java`

```java
// Lines 10–14: Constructor takes generator dependency
private final SudokuGenerator generator;
public GameService(SudokuGenerator generator) {
    this.generator = generator;
}

// Lines 20–27: Generate new puzzle and return solution for later use
public int[][] newPuzzle(Board board) {
    board.resetState();                                          // Clear board
    int[][] solution = generator.generateFullSolution();          // Create complete valid solution
    generator.createPuzzle(board, solution, Board.PREFILLED_COUNT); // Remove cells to form puzzle
    return solution; // Returned so hints/check commands can use it
}
```

---

## SudokuGenerator

**File:** `src/main/java/com/example/sudoku/utils/SudokuGenerator.java`

```java
// Line 13: Random instance for deterministic testing when seeded
private final Random rand;

public SudokuGenerator(Random rand) { this.rand = rand; }

// Lines 20–24: Public API — generates and returns full 9×9 solution
public int[][] generateFullSolution() {
    int[][] solution = new int[Board.SIZE][Board.SIZE];
    fillSolution(solution, 0, 0);
    return solution;
}

// Lines 26–46: Backtracking algorithm to fill grid recursively
private boolean fillSolution(int[][] sol, int r, int c) {
    if (r == Board.SIZE) return true;  // Base case: past last row = complete

    // Lines 29–30: Calculate next row/col (wrap from col 8 → row 9, col 0)
    int nr = (c == Board.SIZE - 1) ? r + 1 : r;
    int nc = (c == Board.SIZE - 1) ? 0 : c + 1;

    // Lines 32–35: Shuffle 1–9 for randomized solutions
    List<Integer> nums = new ArrayList<>();
    for (int i = 1; i <= Board.SIZE; i++) nums.add(i);
    Collections.shuffle(nums, rand);

    // Lines 37–44: Try each number, recurse if valid, backtrack if not
    for (int num : nums) {
        if (canPlace(sol, r, c, num)) {
            sol[r][c] = num;
            if (fillSolution(sol, nr, nc)) return true;
            sol[r][c] = 0; // Backtrack
        }
    }
    return false;
}

// Lines 48–50: Thin delegate to SudokuValidator
private boolean canPlace(int[][] b, int r, int c, int val) {
    return SudokuValidator.isValidMove(b, r, c, val);
}

// Lines 59–107: Convert solution into puzzle by removing cells
public void createPuzzle(Board board, int[][] solution, int prefilledCount) {
    board.copyFromSolution(solution);

    int toRemove = Board.SIZE * Board.SIZE - prefilledCount; // e.g., 81 - 30 = 51 cells to remove

    // Lines 63–68: Collect all cells and shuffle for random removal order
    List<int[]> allCells = new ArrayList<>();
    for (int r = 0; r < Board.SIZE; r++)
        for (int c = 0; c < Board.SIZE; c++)
            allCells.add(new int[] { r, c });
    Collections.shuffle(allCells, rand);

    // Line 75: Reusable buffer to avoid per-iteration allocation
    int[][] candidate = new int[Board.SIZE][Board.SIZE];

    // Lines 77–104: Attempt to remove each cell
    for (int[] cell : allCells) {
        if (removed >= toRemove) break;

        int r = cell[0], c = cell[1];
        if (board.get(r, c) == 0) continue; // Already removed

        board.clear(r, c); // Tentatively remove

        // Lines 91–96: Copy current state into reusable buffer for solution counting
        int[][] snapshot = board.toArrayCopy();
        for (int rr = 0; rr < Board.SIZE; rr++)
            System.arraycopy(snapshot[rr], 0, candidate[rr], 0, Board.SIZE);

        // Line 96: Check uniqueness (stop at 2 solutions)
        int solutions = SudokuValidator.countSolutions(candidate, 2);

        if (solutions != 1) {
            board.set(r, c, solution[r][c]); // Revert: removal created ambiguity
        } else {
            removed++; // Safe to keep removal
        }
    }

    // Line 106: Mark remaining non-zero cells as prefilled
    board.markPrefilledFromBoard();
}
```

---

## SudokuValidator

**File:** `src/main/java/com/example/sudoku/utils/SudokuValidator.java`

```java
// Lines 13–28: Parse cell string ("A3") → array {row, col} (0-indexed)
// Returns null for invalid input
public static int[] parseCell(String s) {
    if (s == null) return null;
    s = s.trim().toUpperCase();
    if (s.length() < 2) return null;
    char rowChar = s.charAt(0);
    if (rowChar < 'A' || rowChar > 'I') return null;
    int r = rowChar - 'A'; // 'A'=0, 'B'=1, ... 'I'=8
    String colPart = s.substring(1);
    try {
        int col = Integer.parseInt(colPart);
        if (col < 1 || col > 9) return null;
        return new int[]{r, col - 1}; // 1-indexed col input → 0-indexed
    } catch (NumberFormatException e) { return null; }
}

// Lines 31–39: Check if placing val at r,c creates any duplicate
// Excludes the cell itself via `(j != c)`, `(i != r)`, `(i != r || j != c)`
public static boolean isValidMove(int[][] board, int r, int c, int val) {
    for (int j = 0; j < Board.SIZE; j++) if (board[r][j] == val && j != c) return false;
    for (int i = 0; i < Board.SIZE; i++) if (board[i][c] == val && i != r) return false;
    int br = (r / 3) * 3, bc = (c / 3) * 3; // Box top-left corner
    for (int i = br; i < br + 3; i++)
        for (int j = bc; j < bc + 3; j++)
            if (board[i][j] == val && (i != r || j != c)) return false;
    return true;
}

// Lines 42–77: Scan entire board and collect all rule violations
public static List<String> validateWholeBoard(int[][] board) {
    // Lines 45–53: Check each row for duplicate values
    for (int r = 0; r < Board.SIZE; r++) {
        boolean[] seen = new boolean[Board.SIZE + 1]; // index 1–9
        for (int c = 0; c < Board.SIZE; c++) {
            int val = board[r][c];
            if (val == 0) continue;
            if (seen[val]) problems.add("Duplicate " + val + " in row " + (char) ('A' + r));
            seen[val] = true;
        }
    }
    // Lines 55–63: Check each column for duplicates
    for (int c = 0; c < Board.SIZE; c++) {
        boolean[] seen = new boolean[Board.SIZE + 1];
        for (int r = 0; r < Board.SIZE; r++) {
            int val = board[r][c];
            if (val == 0) continue;
            if (seen[val]) problems.add("Duplicate " + val + " in column " + (c + 1));
            seen[val] = true;
        }
    }
    // Lines 65–75: Check each 3×3 box for duplicates
    for (int br = 0; br < Board.SIZE; br += 3)
        for (int bc = 0; bc < Board.SIZE; bc += 3) {
            boolean[] seen = new boolean[Board.SIZE + 1];
            for (int r = br; r < br + 3; r++)
                for (int c = bc; c < bc + 3; c++) {
                    int val = board[r][c];
                    if (val == 0) continue;
                    if (seen[val]) problems.add(...);
                    seen[val] = true;
                }
        }
    return problems;
}

// Lines 79–84: True if board is full AND has no violations
public static boolean isCompleteAndValid(int[][] board) {
    for (int r = 0; r < Board.SIZE; r++)
        for (int c = 0; c < Board.SIZE; c++)
            if (board[r][c] == 0) return false;
    return validateWholeBoard(board).isEmpty();
}

// Lines 93–99: Count solutions up to a limit (for uniqueness checking)
// Copies board to avoid mutating the original
public static int countSolutions(int[][] board, int limit) {
    int[][] working = new int[Board.SIZE][Board.SIZE];
    for (int r = 0; r < Board.SIZE; r++)
        System.arraycopy(board[r], 0, working[r], 0, Board.SIZE);
    return countSolutionsBacktrack(working, limit);
}

// Lines 101–120: Backtracking solver with early termination
private static int countSolutionsBacktrack(int[][] board, int limit) {
    int[] next = findBestCell(board); // MRV heuristic
    if (next == null) return validateWholeBoard(board).isEmpty() ? 1 : 0;

    int r = next[0], c = next[1];
    int count = 0;
    for (int val = 1; val <= Board.SIZE; val++) {
        if (!isValidMove(board, r, c, val)) continue;
        board[r][c] = val;
        count += countSolutionsBacktrack(board, limit);
        board[r][c] = 0; // Backtrack
        if (count >= limit) return count; // Early stop
    }
    return count;
}

// Lines 122–144: Find empty cell with Minimum Remaining Values (MRV heuristic)
private static int[] findBestCell(int[][] board) {
    int minCandidates = Integer.MAX_VALUE;
    int[] bestCell = null;
    for (int r = 0; r < Board.SIZE; r++)
        for (int c = 0; c < Board.SIZE; c++)
            if (board[r][c] == 0) {
                int candidates = countCandidates(board, r, c);
                if (candidates < minCandidates) {
                    minCandidates = candidates;
                    bestCell = new int[]{r, c};
                }
            }
    return bestCell;
}

// Lines 146–157: Count how many valid numbers can be placed at (row, col)
private static int countCandidates(int[][] board, int row, int col) {
    int count = 0;
    for (int num = 1; num <= 9; num++)
        if (isValidMove(board, row, col, num)) count++;
    return count;
}
```

---

## Command Framework

### Command Interface
**File:** `src/main/java/com/example/sudoku/commands/Command.java`

```java
public interface Command {
    // Executes command with access to board state and solution
    CommandResult execute(Board board, int[][] solution, Scanner sc);
}
```

### CommandResult
**File:** `src/main/java/com/example/sudoku/commands/CommandResult.java`

```java
public class CommandResult {
    public final boolean success; // false = quit the game
    public final String message;  // text to print to user

    public static CommandResult continueGame(String message) { return new CommandResult(true, message); }
    public static CommandResult quit(String message) { return new CommandResult(false, message); }
}
```

### CommandFactory
**File:** `src/main/java/com/example/sudoku/commands/CommandFactory.java`

```java
// Lines 15–19: No-argument commands (keyword → constructor reference)
private static final Map<String, Supplier<Command>> COMMANDS = Map.of(
    "quit", QuitCommand::new,
    "hint", HintCommand::new,
    "check", CheckCommand::new,
    "help", HelpCommand::new);

// Lines 21–22: One-argument commands (cell-based, e.g., "A1 clear")
private static final Map<String, Function<String, Command>> ARG_COMMANDS = Map.of(
    "clear", ClearCommand::new);

// Lines 24–56: Parse input line into appropriate Command object
public static Command parse(String line, Board board, int[][] solution, Scanner sc) {
    if (line == null || line.isBlank()) return new UnknownCommand();

    String[] parts = line.trim().split("\\s+");
    String cmd = parts[0].toLowerCase();

    // Lines 34–39: Check no-arg commands (quit, hint, check, help)
    Supplier<Command> noArg = COMMANDS.get(cmd);
    if (noArg != null) {
        return parts.length == 1 ? noArg.get() : new UnknownCommand();
    }

    // Lines 42–48: Handle "A5 clear" pattern (cell + action)
    if (parts.length == 2 && "clear".equalsIgnoreCase(parts[1])) {
        Function<String, Command> oneArg = ARG_COMMANDS.get(parts[1].toLowerCase());
        if (oneArg != null) return oneArg.apply(parts[0]);
    }

    // Lines 51–53: Handle "A5 7" pattern (cell + number)
    if (parts.length == 2 && parts[1].matches("\\d+")) {
        return new PlaceCommand(parts[0], parts[1]);
    }

    return new UnknownCommand();
}
```

### PlaceCommand
**File:** `src/main/java/com/example/sudoku/commands/PlaceCommand.java`

```java
// Lines 19–22: Parse cell coordinate
int[] rc = SudokuValidator.parseCell(cell);
if (rc == null) return CommandResult.continueGame("\nInvalid cell reference.\n");

// Lines 24–26: Reject attempts to modify prefilled cells
if (board.isPrefilled(r, c)) return CommandResult.continueGame("\nCannot modify a prefilled cell.\n");

// Lines 28–37: Parse and validate number (1–9)
int val = Integer.parseInt(valueToken);
if (val < 1 || val > 9) return CommandResult.continueGame("\nNumber must be between 1 and 9.\n");

// Lines 39–42: Accept move even if it violates rules (player learns via `check`)
board.set(r, c, val);
return CommandResult.continueGame("\nPlaced " + val + " at " + cell.toUpperCase() + "\n");
```

### ClearCommand
**File:** `src/main/java/com/example/sudoku/commands/ClearCommand.java`

```java
// Lines 16–19: Parse cell reference
// Lines 21–23: Reject clearing prefilled cells
// Lines 24–26: Reject clearing already-empty cells
// Line 27: Perform the clear
board.clear(r, c);
return CommandResult.continueGame("\nCleared " + cell.toUpperCase() + "\n");
```

### HintCommand
**File:** `src/main/java/com/example/sudoku/commands/HintCommand.java`

```java
// Lines 14–17: Find any empty non-prefilled cell
List<int[]> empties = board.getEmptyNonPrefilledCells();
if (empties.isEmpty()) return CommandResult.continueGame("\nNo available hints.\n");

// Line 18: Pick random empty cell
int[] cell = empties.get(HintCommand.RNG.nextInt(empties.size()));

// Lines 20–25: Fill in the solution value and tell the player
board.set(r, c, solution[r][c]);
return CommandResult.continueGame("\nHint: cell " + (char) ('A' + r) + (c + 1) + " = " + solution[r][c] + "\n");
```

### CheckCommand
**File:** `src/main/java/com/example/sudoku/commands/CheckCommand.java`

```java
// Line 14: Validate entire board and collect problems
List<String> problems = SudokuValidator.validateWholeBoard(board.toArrayCopy());

// Lines 17–24: Format output — either "No rule violations" or list of problems
if (problems.isEmpty()) {
    sb.append("No rule violations detected.");
} else {
    sb.append("Problems found:");
    for (String p : problems) sb.append("\n - ").append(p);
}

return CommandResult.continueGame("\n" + sb + "\n");
```

### QuitCommand
```java
// Always returns quit result, breaking the game loop
return CommandResult.quit("\nQuitting. Bye! \n");
```

### HelpCommand
```java
// Returns formatted string listing all available commands
return CommandResult.continueGame(sb.toString());
```

---

## How to Run

```bash
# Run tests
mvn test

# Or use convenience scripts
./run.bat       # Build and run
./run-tests.ps1 # Run tests via PowerShell
```

---

## Data Flow Summary

```
User Input (e.g., "A3 5")
    ↓
CommandFactory.parse() → PlaceCommand instance
    ↓
cmd.execute(board, solution, sc)
    ├── board.set(r, c, val)   ← Mutates state
    └── returns CommandResult(message, success)
    ↓
SudokuGame prints result.message
    ↓
success=false? → quit
success=true && puzzle complete? → new puzzle
Otherwise → repeat loop
```