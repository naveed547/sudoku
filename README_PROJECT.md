# Sudoku CLI (Java) — Logic & Architecture

## Overview
This project implements a **Sudoku command-line game**. It is intentionally structured so each responsibility is easy to test:

- **Board** model: stores the current grid state and which cells are **prefilled** (fixed).
- **SudokuGenerator**: produces a valid full Sudoku solution, then converts it into a playable puzzle by removing values.
- **SudokuUtils**: parsing helpers and Sudoku rule validation (row/column/box duplicates, completion checks).
- **Command framework** (`CommandFactory` + concrete `Command`s): interprets player input (e.g., `A3 5`, `hint`, `check`, `clear A3`).
- **SudokuGame**: interactive loop that ties everything together.

## Recent Changes
- Standardised build/test/run where possible (Maven + shaded jar).
- Fixed HintCommand to use the provided Random.

---

## Key Data Structures

```java
// Board state (conceptual)
int[][] board = new int[9][9];                  // 0 = empty
boolean[][] prefilled = new boolean[9][9];    // true = fixed cell
```

### How commands interact with board state (replace mermaid)

When the player enters a command (place / hint / clear), the command object mutates the `Board` state as follows:

- **PlaceCommand**: attempts to set a value at `(r,c)`.
  - It first checks whether the target cell is **prefilled** (fixed).
  - If not fixed, it updates `board[r][c]` with the chosen value.
- **ClearCommand**: attempts to clear a value at `(r,c)`.
  - It rejects clearing if the cell is **prefilled**.
  - Otherwise it sets `board[r][c] = 0` (empty).
- **HintCommand**: selects an empty, non-prefilled cell and fills it using the generated **solution**.

This is why `Board` exposes both the grid (`board`) and the fixed-cell flags (`prefilled`).

---

## Board


```java
// conceptual Board usage inside commands / game
board.set(r, c, val);
board.clear(r, c);
board.isPrefilled(r, c);
board.getEmptyNonPrefilledCells();
board.markPrefilledFromBoard();
board.toArrayCopy();
```

**State held inside `Board`:**
- `int[9][9] board`
  - Sudoku grid values.
  - Convention: `0` means “empty”.
- `boolean[9][9] prefilled`
  - `true` => the cell is fixed and cannot be changed by player commands.
  - `false` => the player may fill it (via `Place`) or clear it (via `Clear`).

**Key operations (what each method is for):**
1. `get(r, c)` — reads a cell value.
2. `set(r, c, val)` — writes a cell value.
3. `clear(r, c)` — sets a cell back to `0` (empty).
4. `isPrefilled(r, c)` / `setPrefilled(r, c, v)` — fixed-cell flag access.
5. `markPrefilledFromBoard()` — recomputes `prefilled` from current `board` values after generation/puzzle setup.
6. `getEmptyNonPrefilledCells()` — list of cells where `!prefilled` and `board[r][c] == 0`.
7. `toArrayCopy()` — deep copy of the grid for safe validation/solving.

**Design note:** `Board` is meant to be a “dumb state object”; most logic lives in `SudokuUtils` and `SudokuGenerator`.

---

## SudokuGenerator


```java
// conceptual responsibilities
int[][] solution = generateFullSolution();        // backtracking -> valid 9x9
createPuzzle(board, solution, prefilledCount);  // clear random cells -> puzzle
```

This class has two responsibilities:
1. **Generate a complete valid Sudoku solution** (all 81 cells filled).
2. **Create a puzzle** from that solution by clearing exactly `81 - prefilledCount` cells.

```java
// SudokuGenerator.generateFullSolution()
public int[][] generateFullSolution() {
    int[][] solution = new int[Board.SIZE][Board.SIZE];
    fillSolution(solution, 0, 0);
    return solution;
}

// (private helpers used by generateFullSolution)
private boolean fillSolution(int[][] sol, int r, int c) {
    if (r == Board.SIZE) return true;
    int nr = (c == Board.SIZE - 1) ? r + 1 : r;
    int nc = (c == Board.SIZE - 1) ? 0 : c + 1;

    List<Integer> nums = new ArrayList<>();
    for (int i = 1; i <= Board.SIZE; i++) nums.add(i);
    Collections.shuffle(nums, rand);

    for (int num : nums) {
        if (canPlace(sol, r, c, num)) {
            sol[r][c] = num;
            if (fillSolution(sol, nr, nc)) return true;
            sol[r][c] = 0;
        }
    }
    return false;
}

private boolean canPlace(int[][] b, int r, int c, int val) {
    for (int j = 0; j < Board.SIZE; j++) if (b[r][j] == val) return false;
    for (int i = 0; i < Board.SIZE; i++) if (b[i][c] == val) return false;
    int br = (r / 3) * 3, bc = (c / 3) * 3;
    for (int i = br; i < br + 3; i++)
        for (int j = bc; j < bc + 3; j++)
            if (b[i][j] == val) return false;
    return true;
}
```

### 1) `generateFullSolution()`
- Uses **backtracking** to fill the whole 9x9 grid.
- Tries values `1..9` in shuffled order (`Collections.shuffle(nums, rand)`).
- Only places a value if `canPlace(...)` confirms it doesn't break row/col/3x3 rules.
- Correctness: if backtracking succeeds, every placement respects Sudoku constraints.

---

```java
// SudokuGenerator.createPuzzle(Board board, int[][] solution, int prefilledCount)
public void createPuzzle(Board board, int[][] solution, int prefilledCount) {
    board.copyFromSolution(solution);

    int toRemove = Board.SIZE * Board.SIZE - prefilledCount;
    List<int[]> allCells = new ArrayList<>();
    for (int r = 0; r < Board.SIZE; r++) {
        for (int c = 0; c < Board.SIZE; c++) {
            allCells.add(new int[]{r, c});
        }
    }
    Collections.shuffle(allCells, rand);

    int removed = 0;

    // Reuse a single candidate buffer to avoid per-iteration allocations.
    int[][] candidate = new int[Board.SIZE][Board.SIZE];

    for (int[] cell : allCells) {
        if (removed >= toRemove) break;

        int r = cell[0];
        int c = cell[1];
        if (board.get(r, c) == 0) continue;

        // Try removing
        board.clear(r, c);

        // Copy board state into the reusable candidate buffer.
        int[][] snapshot = board.toArrayCopy();
        for (int rr = 0; rr < Board.SIZE; rr++) {
            System.arraycopy(snapshot[rr], 0, candidate[rr], 0, Board.SIZE);
        }

        // Uniqueness check (stop after 2 solutions)
        int solutions = SudokuUtils.countSolutions(candidate, 2);

        if (solutions != 1) {
            // Revert removal
            board.set(r, c, solution[r][c]);
        } else {
            removed++;
        }
    }

    board.markPrefilledFromBoard();
}
```

### 2) `createPuzzle(board, solution, prefilledCount)`
Step-by-step:
1. Copy the full `solution` into `board`.
2. Shuffle all cell positions and attempt removals until the target number of prefilled cells remains.
3. For each attempted removal:
   - clear the cell (`board.clear(r, c)`).
   - check uniqueness with `SudokuUtils.countSolutions(candidate, 2)`.
   - revert the removal unless the puzzle would still have **exactly 1** solution.
4. Call `board.markPrefilledFromBoard()` so all remaining non-zero cells become fixed.

File reference:
- `src/main/java/com/example/sudoku/utils/SudokuGenerator.java`


---

## SudokuUtils


```java
// conceptual responsibilities
int[] rc = parseCell("A3");
boolean ok = isValidMove(board, r, c, val);
List<String> problems = validateWholeBoard(board);
boolean done = isCompleteAndValid(board);
int solutions = countSolutions(board, limit);
```

This class is the **rules engine + parser** for the CLI.

```java
// SudokuUtils.parseCell(String s)
public static int[] parseCell(String s) {
    if (s == null) return null;
    s = s.trim().toUpperCase();
    if (s.length() < 2) return null;
    char rowChar = s.charAt(0);
    if (rowChar < 'A' || rowChar > 'I') return null;
    int r = rowChar - 'A';
    String colPart = s.substring(1);
    try {
        int col = Integer.parseInt(colPart);
        if (col < 1 || col > 9) return null;
        return new int[]{r, col - 1};
    } catch (NumberFormatException e) {
        return null;
    }
}
```

### `parseCell("A3")`
- Converts `A..I` + `1..9` into numeric coordinates.
- Returns `null` for invalid input.


Used by:
- `PlaceCommand` (cell + number)
- `ClearCommand` (cell only)

```java
// SudokuUtils.isValidMove(int[][] board, int r, int c, int val)
public static boolean isValidMove(int[][] board, int r, int c, int val) {
    for (int j = 0; j < Board.SIZE; j++) if (board[r][j] == val && j != c) return false;
    for (int i = 0; i < Board.SIZE; i++) if (board[i][c] == val && i != r) return false;
    int br = (r / 3) * 3, bc = (c / 3) * 3;
    for (int i = br; i < br + 3; i++)
        for (int j = bc; j < bc + 3; j++)
            if (board[i][j] == val && (i != r || j != c)) return false;
    return true;
}
```

### `isValidMove(board, r, c, val)`
- Checks no duplicate `val` exists in:
  - the row
  - the column
  - the 3x3 box
- Used by command placement logic and backtracking helpers.


```java
// SudokuUtils.validateWholeBoard(int[][] board)
public static List<String> validateWholeBoard(int[][] board) {
    List<String> problems = new ArrayList<>();
    // rows
    for (int r = 0; r < Board.SIZE; r++) {
        boolean[] seen = new boolean[Board.SIZE + 1];
        for (int c = 0; c < Board.SIZE; c++) {
            int val = board[r][c];
            if (val == 0) continue;
            if (seen[val]) problems.add("Duplicate " + val + " in row " + (char) ('A' + r));
            seen[val] = true;
        }
    }
    // cols
    for (int c = 0; c < Board.SIZE; c++) {
        boolean[] seen = new boolean[Board.SIZE + 1];
        for (int r = 0; r < Board.SIZE; r++) {
            int val = board[r][c];
            if (val == 0) continue;
            if (seen[val]) problems.add("Duplicate " + val + " in column " + (c + 1));
            seen[val] = true;
        }
    }
    // boxes
    for (int br = 0; br < Board.SIZE; br += 3)
        for (int bc = 0; bc < Board.SIZE; bc += 3) {
            boolean[] seen = new boolean[Board.SIZE + 1];
            for (int r = br; r < br + 3; r++)
                for (int c = bc; c < bc + 3; c++) {
                    int val = board[r][c];
                    if (val == 0) continue;
                    if (seen[val]) problems.add("Duplicate " + val + " in 3x3 box starting at " + (char) ('A' + br) + (bc + 1));
                    seen[val] = true;
                }
        }
    return problems;
}
```

### `validateWholeBoard(board)`
- Scans the whole grid for duplicates and returns user-friendly problem strings.
- Produces a list:
  - empty list => no duplicates
  - non-empty => at least one rule violation

Used by:
- `CheckCommand`

```java
// SudokuUtils.isCompleteAndValid(int[][] board)
public static boolean isCompleteAndValid(int[][] board) {
    for (int r = 0; r < Board.SIZE; r++)
        for (int c = 0; c < Board.SIZE; c++)
            if (board[r][c] == 0) return false;
    return validateWholeBoard(board).isEmpty();
}
```

### `isCompleteAndValid(board)`
- Win condition:
  - no zeros (grid complete)
  - no duplicates (validation returns empty list)

```java
// SudokuUtils.countSolutions(int[][] board, int limit)
public static int countSolutions(int[][] board, int limit) {
    int[][] working = new int[Board.SIZE][Board.SIZE];
    for (int r = 0; r < Board.SIZE; r++) {
        System.arraycopy(board[r], 0, working[r], 0, Board.SIZE);
    }
    return countSolutionsBacktrack(working, limit);
}
```

### `countSolutions(board, limit)`
- Counts how many valid solutions exist for a partially filled board.
- Uses early stopping once `limit` is reached.

File reference:

- `src/main/java/com/example/sudoku/utils/SudokuUtils.java`

---

## Command Framework (CLI)


```java
// conceptual command routing
if input == "quit"  -> QuitCommand
if input == "hint"  -> HintCommand
if input == "check" -> CheckCommand
if input == "help"  -> HelpCommand
if input starts "clear " -> ClearCommand
if input like "<cell> <number>" -> PlaceCommand
```

### `Command` interface
- `boolean execute(Board board, int[][] solution, Scanner sc)`
  - `true` => continue loop
  - `false` => stop loop (used by `QuitCommand`)

### `CommandFactory`
- Parses the raw input string and returns the proper `Command` instance.

### Concrete commands (behavior summary)
- **PlaceCommand**
  - parses cell + number
  - rejects if the cell is prefilled
  - sets the cell value; duplicates are reported via `check`
- **ClearCommand**
  - parses cell
  - rejects if prefilled or already empty
  - clears cell to `0`
- **HintCommand**
  - finds empty non-prefilled cells
  - randomly chooses one and fills it using the solution value
- **CheckCommand**
  - calls `SudokuUtils.validateWholeBoard(board.toArrayCopy())`
  - prints success or a list of problems
- **HelpCommand / UnknownCommand / QuitCommand**
  - help prints commands
  - unknown prints error
  - quit stops loop

File references:
- `src/main/java/com/example/sudoku/commands/CommandFactory.java`
- `src/main/java/com/example/sudoku/commands/*Command.java`

---

## SudokuGame (Main Game Loop)


```java
// conceptual game flow
print welcome
Board board = new Board();
int[][] solution = board.generateAndSetPuzzle();

loop:
  board.display();
  if SudokuUtils.isCompleteAndValid(board.toArrayCopy()):
    win message; optionally reset puzzle
  commandLine = read input
  cmd = CommandFactory.parse(commandLine)
  keepRunning = cmd.execute(board, solution, scanner)
  if !keepRunning: break
```

**Control flow highlights:**
1. Create a new puzzle (board + solution).
2. Display the board.
3. If complete & valid, print completion and reset/continue as implemented.
4. Otherwise:
   - read a command line
   - normalize commas into spaces
   - route via `CommandFactory`
   - execute command

File reference:
- `src/main/java/com/example/sudoku/SudokuGame.java`


---

## Tests Added
- `src/test/java/com/example/sudoku/SudokuGameTest.java`
  - smoke test verifying welcome/quit behavior

Other tests verify:
- `Board` behavior
- `SudokuGenerator` correctness
- `SudokuUtils` parsing/validation/solution counting
- command behavior (place/clear/hint/check, etc.)

---

## How to Run
- `mvn test`
- or run the project via `run.bat` / `run-tests.ps1`

---

## Quick Mental Model
1. **Generator** creates a correct full solution, then removes cells.
2. **Board** stores current state + fixed-cell flags.
3. **Utils** performs parsing and rule validation.
4. **Commands** mutate the board in atomic steps.
5. **Game loop** prints, checks win condition, parses input, executes commands.
