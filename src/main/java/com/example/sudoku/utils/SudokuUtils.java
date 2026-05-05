package com.example.sudoku.utils;

import java.util.*;
import com.example.sudoku.Board;


/**
 * Utility functions for Sudoku logic: validation, move checks, parsing.
 * Kept stateless and static so tests can call them directly.
 */
public class SudokuUtils {
    // Parse cell string like "A3" or "a3" -> {row,col} or return null if invalid
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

    // Check whether placing val at r,c on given board would create duplicates
    public static boolean isValidMove(int[][] board, int r, int c, int val) {
        for (int j = 0; j < Board.SIZE; j++) if (board[r][j] == val && j != c) return false;
        for (int i = 0; i < Board.SIZE; i++) if (board[i][c] == val && i != r) return false;
        int br = (r / 3) * 3, bc = (c / 3) * 3;
        for (int i = br; i < br + 3; i++)
            for (int j = bc; j < bc + 3; j++)
                if (board[i][j] == val && (i != r || j != c)) return false;
        return true;
    }

    // Validate the whole board and return a list of human readable problems found
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

    public static boolean isCompleteAndValid(int[][] board) {
        for (int r = 0; r < Board.SIZE; r++)
            for (int c = 0; c < Board.SIZE; c++)
                if (board[r][c] == 0) return false;
        return validateWholeBoard(board).isEmpty();
    }
}
