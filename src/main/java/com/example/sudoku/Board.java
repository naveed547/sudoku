package com.example.sudoku;

import java.util.*;
import com.example.sudoku.utils.SudokuValidator;

public class Board {
    public static final int SIZE = 9;
    public static final int PREFILLED_COUNT = 30;
    public static final int MAX_HINT_ALLOWED = 5;
    private final int[][] board = new int[SIZE][SIZE];
    private final boolean[][] prefilled = new boolean[SIZE][SIZE];

    private int[][] solution;
    private boolean puzzleStarted = false;
    private int hintCountLeft = MAX_HINT_ALLOWED;

    public boolean consumeHint() {
        if (hintCountLeft == 0) return false;
        hintCountLeft--;
        return true;
    }

    public int getHintCountLeft() { return hintCountLeft; }
    public void setSolution(int[][] solution) { this.solution = solution; }
    public int[][] getSolution() { return solution; }

    public void copyFromSolution(int[][] solution) {
        for (int i = 0; i < SIZE; i++) System.arraycopy(solution[i], 0, board[i], 0, SIZE);
    }

    public void resetState() {
        for (int r = 0; r < SIZE; r++) {
            Arrays.fill(board[r], 0);
            Arrays.fill(prefilled[r], false);
        }
        this.setPuzzleStarted(false);
        this.hintCountLeft = MAX_HINT_ALLOWED;
    }

    public void restartCurrentPuzzle() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (!prefilled[r][c]) {
                    board[r][c] = 0;
                }
            }
        }
        this.puzzleStarted = false;
        this.hintCountLeft = MAX_HINT_ALLOWED;
    }

    public boolean isPuzzleStarted() { return puzzleStarted; }
    public void setPuzzleStarted(boolean v) { this.puzzleStarted = v; }
    public int get(int r, int c) { return board[r][c]; }
    public void set(int r, int c, int val) { board[r][c] = val; }
    public void clear(int r, int c) { board[r][c] = 0; }
    public boolean isPrefilled(int r, int c) { return prefilled[r][c]; }
    public void setPrefilled(int r, int c, boolean v) { prefilled[r][c] = v; }

    public boolean placeValue(int r, int c, int val) {
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE || isPrefilled(r, c) || val < 1 || val > 9) return false;
        set(r, c, val);
        setPuzzleStarted(true);
        return true;
    }

    public boolean applyHint(int r, int c, int val) { return placeValue(r, c, val); }

    public boolean clearCell(int r, int c) {
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE || isPrefilled(r, c) || board[r][c] == 0) return false;
        clear(r, c);
        setPuzzleStarted(true);
        return true;
    }

    public boolean isSolved() { return SudokuValidator.isCompleteAndValid(board); }

    public List<int[]> getEmptyNonPrefilledCells() {
        var empties = new ArrayList<int[]>();
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (!prefilled[r][c] && board[r][c] == 0) empties.add(new int[]{r, c});
        return empties;
    }

    public int[][] toArrayCopy() {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, SIZE);
        }
        return copy;
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
