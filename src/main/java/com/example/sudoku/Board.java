package com.example.sudoku;

import java.util.*;

import com.example.sudoku.utils.SudokuGenerator;

/**
 * Board model
 * Provides small helpers for reading/modifying cells and querying empties.
 */
public class Board {
    public static final int SIZE = 9;
    public static final int PREFILLED_COUNT = 30;

    private final int[][] board = new int[SIZE][SIZE];
    private final boolean[][] prefilled = new boolean[SIZE][SIZE];

    private boolean PUZZLE_STARTED = false;


    // Copy solution values into the board (used when creating a puzzle)
    public void copyFromSolution(int[][] solution) {
        for (int i = 0; i < SIZE; i++) System.arraycopy(solution[i], 0, board[i], 0, SIZE);
    }

    /**
     * Returns the full generated solution so callers can use it for validation/hints.
     */
    public int[][] generateAndSetPuzzle() {
        this.resetBoard();
        SudokuGenerator gen = new SudokuGenerator(new Random());
        int[][] solution = gen.generateFullSolution();
        gen.createPuzzle(this, solution, PREFILLED_COUNT);
        return solution;
    }

    public int get(int r, int c) { return board[r][c]; }
    public void set(int r, int c, int val) { board[r][c] = val; }
    public void clear(int r, int c) { board[r][c] = 0; }
    public boolean isPrefilled(int r, int c) { return prefilled[r][c]; }
    public void setPrefilled(int r, int c, boolean v) { prefilled[r][c] = v; }

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

    public void display() {
        StringBuilder sb = new StringBuilder();

        sb.append(PUZZLE_STARTED ? "Current grid:" : "Here is your puzzle:").append('\n');
        String colsHeader = "   1 2 3 | 4 5 6 | 7 8 9 ";
        sb.append(colsHeader).append('\n');
        sb.append("   ------+-------+------- ").append('\n');

        for (int r = 0; r < 9; r++) {
            char rowLabel = (char) ('A' + r);
            sb.append(rowLabel).append("  ");

            for (int c = 0; c < 9; c++) {
                int val = get(r, c);
                String cell = (val == 0) ? "_" : String.valueOf(val);
                sb.append(cell).append(" ");
                if (c % 3 == 2 && c < 8) sb.append("| ");
            }

            sb.append('\n');
            if (r % 3 == 2 && r < 8) {
                sb.append("   ------+-------+------- ").append('\n');
            }
        }

        System.out.print(sb.toString()+ "\n");
        PUZZLE_STARTED = true;
    }

    private void resetBoard() {
        // reset board and prefilled markers
        for (int r = 0; r < SIZE; r++) {
            Arrays.fill(board[r], 0);
            Arrays.fill(prefilled[r], false);
        }
    }
}

