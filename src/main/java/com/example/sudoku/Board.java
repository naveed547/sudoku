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
        puzzleStarted = false;
    }

    public boolean isPuzzleStarted() {
        return puzzleStarted;
    }

    public void setPuzzleStarted(boolean v) {
        this.puzzleStarted = v;
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

