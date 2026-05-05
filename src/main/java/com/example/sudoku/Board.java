package com.example.sudoku;

import java.util.*;

/**
 * Board model: holds current board values and prefilled flags.
 * Provides small helpers for reading/modifying cells and querying empties.
 */
public class Board {
    public static final int SIZE = 9;
    private final int[][] board = new int[SIZE][SIZE];
    private final boolean[][] prefilled = new boolean[SIZE][SIZE];

    public Board() {
        // initialized to zeros / false
    }

    // Copy solution values into the board (used when creating a puzzle)
    public void copyFromSolution(int[][] solution) {
        for (int i = 0; i < SIZE; i++) System.arraycopy(solution[i], 0, board[i], 0, SIZE);
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

    public void display(boolean first) {
        System.out.println(first ? "Here is your puzzle:" : "Current grid:");
        String colsHeader = "   1 2 3 | 4 5 6 | 7 8 9 ";
        System.out.println(colsHeader);
        System.out.println("   ------+-------+------- ");
        for (int r = 0; r < 9; r++) {
            char rowLabel = (char) ('A' + r);
            System.out.print(rowLabel + "  ");
            for (int c = 0; c < 9; c++) {
                int val = get(r, c);
                String cell = (val == 0) ? "_" : String.valueOf(val);
                System.out.print(cell + " ");
                if (c % 3 == 2 && c < 8) System.out.print("| ");
            }
            System.out.println();
            if (r % 3 == 2 && r < 8) {
                System.out.println("   ------+-------+------- ");
            }
        }
    }
}
