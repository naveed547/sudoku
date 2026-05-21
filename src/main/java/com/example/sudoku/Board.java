package com.example.sudoku;

import java.util.*;

/**
     * Board model
     * Provides small helpers for reading/modifying cells and querying empties.
     */
public class Board {
    public static final int SIZE = 9;
    public static final int PREFILLED_COUNT = 30;

    private final int[][] board = new int[SIZE][SIZE];
    private final boolean[][] prefilled = new boolean[SIZE][SIZE];

    private boolean puzzleStarted = false;

    private int[][] solution;

    public void setSolution(int[][] solution) {
        this.solution = solution;
    }

    public int[][] getSolution() {
        return solution;
    }

    // Copy solution values into the board (used when creating a puzzle)
    public void copyFromSolution(int[][] solution) {
        for (int i = 0; i < SIZE; i++) System.arraycopy(solution[i], 0, board[i], 0, SIZE);
    }

    /**
     * Reset board state before generating a new puzzle.
     * (Generation flow lives in GameService now.)
     */
    public void resetState() {
        // reset board and prefilled markers
        for (int r = 0; r < SIZE; r++) {
            Arrays.fill(board[r], 0);
            Arrays.fill(prefilled[r], false);
        }
        this.setPuzzleStarted(false);
    }

    public boolean isPuzzleStarted() {
        return puzzleStarted;
    }

    public void setPuzzleStarted(boolean v) {
        this.puzzleStarted = v;
    }

    public int get(int r, int c) { return board[r][c]; }

    public void printWelcome() {
        printLine("Welcome to Sudoku! (Prefilled cells are fixed and cannot be changed)");
    }

    public void printCompletionSuccess() {
        StringBuilder sb = new StringBuilder();
        sb.append("You have successfully completed the Sudoku puzzle!");
        sb.append(System.lineSeparator());
        sb.append("Press ENTER to play again...");
        printLine(sb.toString());
    }

    /**
     * Render the board to stdout using the legacy display formatting.
     * @param puzzleStarted whether to show "Current grid:" instead of "Here is your puzzle:"
     */
    public void render(boolean puzzleStarted) {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append(puzzleStarted ? "Current grid:" : "Here is your puzzle:").append('\n');
        String colsHeader = "   1 2 3 | 4 5 6 | 7 8 9 ";
        sb.append(colsHeader).append('\n');
        sb.append("   ------+-------+------- ").append('\n');

        for (int r = 0; r < SIZE; r++) {
            char rowLabel = (char) ('A' + r);
            sb.append(rowLabel).append("  ");

            for (int c = 0; c < SIZE; c++) {
                int val = board[r][c];
                String cell = (val == 0) ? "_" : String.valueOf(val);
                sb.append(cell).append(" ");
                if (c % 3 == 2 && c < 8) sb.append("| ");
            }

            sb.append('\n');
            if (r % 3 == 2 && r < 8) {
                sb.append("   ------+-------+------- ").append('\n');
            }
        }

        sb.append("\nEnter command (eg: A3 4, C5 clear, hint, check, quit, help): ");
        printLine(sb.toString());
    }

    private void printLine(String s) {
        System.out.println(s);
    }

    // Low-level mutation helpers (kept for gradual migration)
    public void set(int r, int c, int val) { board[r][c] = val; }

    // Low-level mutation helpers (kept for gradual migration)
    public void clear(int r, int c) { board[r][c] = 0; }

    public boolean isPrefilled(int r, int c) { return prefilled[r][c]; }

    public void setPrefilled(int r, int c, boolean v) { prefilled[r][c] = v; }

    /**
     * Domain operation: place a value onto a non-prefilled cell.
     *
     * @return true if the move was applied; false otherwise.
     */
    public boolean placeValue(int r, int c, int val) {
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) {
            return false;
        }
        if (isPrefilled(r, c)) {
            return false;
        }
        if (val < 1 || val > 9) {
            return false;
        }
        set(r, c, val);
        return true;
    }

    /**
     * Domain operation: apply a hint value onto a non-prefilled empty cell.
     *
     * @return true if the hint was applied; false otherwise.
     */
    public boolean applyHint(int r, int c, int val) {
        // Hint is a special case of placing a value with the same invariants as user entry.
        return placeValue(r, c, val);
    }


    /**
     * Domain operation: clear a user-entered value from a cell.
     *
     * @return true if the cell was cleared; false otherwise.
     */
    public boolean clearCell(int r, int c) {
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) {
            return false;
        }
        if (isPrefilled(r, c)) {
            return false;
        }
        if (board[r][c] == 0) {
            return false;
        }
        clear(r, c);
        return true;
    }

    /**
     * @return true if the puzzle is solved (complete and valid).
     */
    public boolean isSolved() {
        return com.example.sudoku.utils.SudokuValidator.isCompleteAndValid(toArrayCopy());
    }



    public List<int[]> getEmptyNonPrefilledCells() {
        List<int[]> empties = new ArrayList<>();
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (!prefilled[r][c] && board[r][c] == 0) empties.add(new int[]{r, c});
        return empties;
    }

    public int[][] toArrayCopy() {
        int[][] out = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) System.arraycopy(board[i], 0, out[i], 0, SIZE);
        return out;
    }

    public void markPrefilledFromBoard() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                prefilled[r][c] = board[r][c] != 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board1 = (Board) o;
        return Arrays.deepEquals(board, board1.board) && Arrays.deepEquals(prefilled, board1.prefilled);
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.deepHashCode(board) + Arrays.deepHashCode(prefilled);
    }
}

