package com.example.sudoku.utils;

import java.util.*;
import com.example.sudoku.Board;

public class SudokuValidator {

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

    public static boolean isValidMove(int[][] board, int r, int c, int val) {
        // Check Row and Column
        for (int i = 0; i < Board.SIZE; i++) {
            if (board[r][i] == val && i != c) return false;
            if (board[i][c] == val && i != r) return false;
        }
        // Check 3x3 Box
        int br = (r / 3) * 3, bc = (c / 3) * 3;
        for (int i = br; i < br + 3; i++) {
            for (int j = bc; j < bc + 3; j++) {
                if (board[i][j] == val && (i != r || j != c)) return false;
            }
        }
        return true;
    }

    public static ValidationResult validateWholeBoard(int[][] board) {
        var errors = new ArrayList<ValidationResult.ValidationError>();
        
        for (int r = 0; r < Board.SIZE; r++) {
            boolean[] seenR = new boolean[10], seenC = new boolean[10];
            for (int c = 0; c < Board.SIZE; c++) {
                if (board[r][c] != 0 && seenR[board[r][c]]) 
                    errors.add(new ValidationResult.ValidationError(ValidationResult.ErrorType.ROW, board[r][c], r, -1));
                if (board[c][r] != 0 && seenC[board[c][r]]) 
                    errors.add(new ValidationResult.ValidationError(ValidationResult.ErrorType.COLUMN, board[c][r], -1, r));
                
                if (board[r][c] != 0) seenR[board[r][c]] = true;
                if (board[c][r] != 0) seenC[board[c][r]] = true;
            }
        }

        for (int br = 0; br < Board.SIZE; br += 3)
            for (int bc = 0; bc < Board.SIZE; bc += 3) {
                boolean[] seen = new boolean[10];
                for (int r = br; r < br + 3; r++)
                    for (int c = bc; c < bc + 3; c++) {
                        int val = board[r][c];
                        if (val != 0) {
                            if (seen[val]) errors.add(new ValidationResult.ValidationError(ValidationResult.ErrorType.BOX, val, br, bc));
                            seen[val] = true;
                        }
                    }
            }
        return new ValidationResult(errors);
    }

    public static boolean isCompleteAndValid(int[][] board) {
        return Arrays.stream(board).flatMapToInt(Arrays::stream).noneMatch(v -> v == 0) && !hasProblems(board);
    }

    private static boolean hasProblems(int[][] board) {
        for (int r = 0; r < Board.SIZE; r++) {
            if (hasDuplicate(board, r, true)) return true;
            if (hasDuplicate(board, r, false)) return true;
        }
        for (int br = 0; br < Board.SIZE; br += 3)
            for (int bc = 0; bc < Board.SIZE; bc += 3)
                if (hasBoxDuplicate(board, br, bc)) return true;
        return false;
    }

    private static boolean hasDuplicate(int[][] board, int index, boolean isRow) {
        boolean[] seen = new boolean[10];
        for (int i = 0; i < Board.SIZE; i++) {
            int val = isRow ? board[index][i] : board[i][index];
            if (val != 0 && seen[val]) return true;
            if (val != 0) seen[val] = true;
        }
        return false;
    }

    /**
     * Counts the number of valid solutions for the given (partially filled) board.
     * Uses backtracking and stops as soon as it reaches {@code limit}.
     *
     * @param board board values, where 0 means empty
     * @param limit maximum number of solutions to search for (for early stopping)
     */
    public static int countSolutions(int[][] board, int limit) {
        int[][] working = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);
        return countSolutionsBacktrack(working, limit);
    }

    private static int countSolutionsBacktrack(int[][] board, int limit) {
        int[] next = findBestCell(board);
        if (next == null) return hasProblems(board) ? 0 : 1;

        int r = next[0], c = next[1];
        int count = 0;
        for (int val = 1; val <= Board.SIZE; val++) {
            if (isValidMove(board, r, c, val)) {
                board[r][c] = val;
                count += countSolutionsBacktrack(board, limit);
                board[r][c] = 0;
                if (count >= limit) return count;
            }
        }
        return count;
    }

    private static int[] findBestCell(int[][] board) {
        int minCandidates = Integer.MAX_VALUE;
        int[] bestCell = null;
    
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (board[r][c] != 0) continue;
    
                int candidates = countCandidates(board, r, c);
                if (candidates < minCandidates) {
                    minCandidates = candidates; bestCell = new int[]{r, c};
                }
            }
        }
        return bestCell;
    }

    private static int countCandidates(int[][] board, int row, int col) {
        int count = 0;
        for (int num = 1; num <= 9; num++) {
            if (isValidMove(board, row, col, num)) count++;
        }
        return count;
    }

    private static boolean hasBoxDuplicate(int[][] board, int br, int bc) {
        boolean[] seen = new boolean[10];
        for (int r = br; r < br + 3; r++) {
            for (int c = bc; c < bc + 3; c++) {
                int val = board[r][c];
                if (val != 0 && seen[val]) return true;
                if (val != 0) seen[val] = true;
            }
        }
        return false;
    }
}
