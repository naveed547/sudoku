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
| `SudokuGenerator` | Generates valid puzzles using a backtracking algorithm |
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
    new SudokuGame().run();
}
```

```java
// Lines 21–60: Main game loop
private void start() {
    // Line 23: Initialize Service and generate first puzzle
    GameService gameService = new GameService(new SudokuGenerator());
    Board board = gameService.startNewGame();

    // Line 26: Print welcome banner using static utility
    System.out.print(BoardRenderer.getWelcomeMessage());

    // Line 30: Scanner auto-closes at end of try block
    try (Scanner sc = new Scanner(System.in)) {
        while (true) {
            // Line 32: Render current board state and prompt
            System.out.print(BoardRenderer.renderToString(board));
            System.out.print(BoardRenderer.getPrompt(board));

            // Line 44: Read player input
            String line = sc.nextLine().trim();

            // Line 48: Allow comma as separator (e.g., "A3, 5" → "A3 5")
            line = line.replace(",", " ").trim();

            // Line 49: Parse input string into a Command object
            Command cmd = CommandFactory.parse(line, board, gameService);

            // Line 50: Execute command, get result
            CommandResult result = cmd.execute(board);

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

// Line 12: Limit on user hints
public static final int MAX_HINT_ALLOWED = 5;

// Line 13: 2D array storing cell values (0 = empty)
private final int[][] board = new int[SIZE][SIZE];

// Line 14: Parallel array marking which cells are fixed/prefilled
private final boolean[][] prefilled = new boolean[SIZE][SIZE];

// Line 15: Stored solution for validation and hints
private int[][] solution;

// Line 16: Tracks if player has made any move (changes header from "puzzle" to "grid")
private boolean puzzleStarted = false;

// Lines 19–21: Copy solution values into board (used during puzzle generation)
public void copyFromSolution(int[][] solution) { ... }

// Lines 27–34: Reset all state for new puzzle
public void resetState() { ... }

// Lines 36–42: puzzleStarted getter/setter
public boolean isPuzzleStarted() { return puzzleStarted; }
public void setPuzzleStarted(boolean v) { this.puzzleStarted = v; }

// Lines 44–48: Basic cell accessors
public int get(int r, int c) { ... }
public boolean placeValue(int r, int c, int val) { ... }
public boolean clearCell(int r, int c) { ... }
public boolean applyHint(int r, int c, int val) { ... }
public boolean isPrefilled(int r, int c) { return prefilled[r][c]; }
public boolean isSolved() { ... }
public int getHintCountLeft() { ... }
public int[][] getSolution() { return solution; }
public void setSolution(int[][] sol) { this.solution = sol; }
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
**Key Changes:** Methods are now static and return `String` for the game loop to print.

```java
// Lines 9-11: Welcome banner string
public static String getWelcomeMessage() { ... }

// Lines 13-15: Success message string
public static String getCompletionMessage() { ... }

// Lines 17-20: Dynamic prompt showing remaining hints
public static String getPrompt(Board board) { ... }

// Lines 22–50: Main board rendering as a string
public static String renderToString(Board board) {
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
    }
    return sb.toString();
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
// Line 25: Prepare board with new puzzle data
public void prepareNewPuzzle(Board board) {
    board.resetState();
    int[][] solution = generator.generateFullSolution();
    generator.createPuzzle(board, solution, Board.PREFILLED_COUNT);
    board.setSolution(solution);
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
    CommandResult execute(Board board);
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
// Line 18: Map-based registry for single-word commands
private static final Map<String, Function<String[], Command>> SINGLE_WORD_COMMANDS = Map.of(
        "quit", QuitCommand::parse,
        "hint", HintCommand::parse,
        "solve", SolveCommand::parse,
        "restart", RestartCommand::parse,
        ...
);

public static Command parse(String line, Board board, GameService gameService) {
    // Line 31: Empty input triggers Refresh (or New Game if solved)
    if (line == null || line.isBlank()) return new RefreshCommand(gameService);

    String[] tokens = line.trim().split("\\s+");
    String firstToken = tokens[0].toLowerCase();

    // Line 39: Check registry
    if (tokens.length == 1 && SINGLE_WORD_COMMANDS.containsKey(firstToken)) {
        return SINGLE_WORD_COMMANDS.get(firstToken).apply(tokens);
    }

    // Line 45: Block placement/clearing if puzzle is already solved
    if (board != null && board.isSolved()) {
        return b -> CommandResult.continueGame("\nPuzzle solved! Press ENTER...");
    }

    if (ClearCommand.parse(tokens) != null) return ClearCommand.parse(tokens);
    if (PlaceCommand.parse(tokens) != null) return PlaceCommand.parse(tokens);

    return new UnknownCommand();
}
```

### PlaceCommand
**File:** `src/main/java/com/example/sudoku/commands/PlaceCommand.java`

```java
int[] rc = SudokuValidator.parseCell(cell);
if (rc == null) return CommandResult.continueGame("\nInvalid cell reference.\n");

if (board.isPrefilled(r, c)) return CommandResult.continueGame("\nCannot modify a prefilled cell.\n");

int val;
try { val = Integer.parseInt(valueToken); }
catch (NumberFormatException e) {
    return CommandResult.continueGame("\nSecond token must be a number 1-9.\n");
}

if (val < 1 || val > 9) {
    return CommandResult.continueGame("\nNumber must be between 1 and 9.\n");
}

boolean ok = board.placeValue(r, c, val);
if (!ok) {
    return CommandResult.continueGame("\nCannot place value on that cell.\n");
}

return CommandResult.continueGame("\nPlaced " + val + " at " + cell.toUpperCase() + "\n");
```

### ClearCommand
**File:** `src/main/java/com/example/sudoku/commands/ClearCommand.java`

```java
int[] rc = SudokuValidator.parseCell(cell);
if (rc == null) return CommandResult.continueGame("\nInvalid cell reference.\n");

if (board.isPrefilled(r, c)) {
    return CommandResult.continueGame("\nCannot clear a prefilled cell.\n");
}

if (board.get(r, c) == 0) {
    return CommandResult.continueGame("\nCell already empty.\n");
}

boolean ok = board.clearCell(r, c);
if (!ok) {
    return CommandResult.continueGame("\nCell could not be cleared.\n");
}

return CommandResult.continueGame("\nCleared " + cell.toUpperCase() + "\n");
```

### HintCommand
**File:** `src/main/java/com/example/sudoku/commands/HintCommand.java`

```java
List<int[]> empties = board.getEmptyNonPrefilledCells();
if (empties.isEmpty()) {
    return CommandResult.continueGame("\nNo available hints.\n");
}

int[] cell = empties.get(HintCommand.RNG.nextInt(empties.size()));

int r = cell[0], c = cell[1];
int val = board.getSolution()[r][c];

boolean ok = board.applyHint(r, c, val);
if (!ok) {
    return CommandResult.continueGame("\nNo available hints.\n");
}

return CommandResult.continueGame(
    "\nHint: cell " + (char) ('A' + r) + (c + 1) + " = " + val + "\n"
);
```

### CheckCommand
**File:** `src/main/java/com/example/sudoku/commands/CheckCommand.java`

```java
List<String> problems = SudokuValidator.validateWholeBoard(board.toArrayCopy());

StringBuilder sb = new StringBuilder();
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
return CommandResult.quit("\nQuitting. Bye! \n");
```

### HelpCommand
```java
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
CommandFactory.parse(line, board) → PlaceCommand instance
    ↓
cmd.execute(board) 
    ├── mutates board state (place/clear/hint)
    └── returns CommandResult(message, success)
    ↓
SudokuGame prints result.message
    ↓
success=false? → quit
success=true && puzzle complete? → new puzzle
Otherwise → repeat loop
```
