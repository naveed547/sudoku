package com.example.sudoku.utils;

import java.util.*;

import com.example.sudoku.Board;

/**
 * Responsible for generating a full Sudoku solution and turning it into a puzzle
 * by removing cells while leaving a requested number of prefilled cells.
 */
public class SudokuGenerator {
    private final Random rand;

    public SudokuGenerator(Random rand) {
        this.rand = rand;
    }

    /** Generate a full valid solution (9x9). */
    public int[][] generateFullSolution() {
        int[][] solution = new int[Board.SIZE][Board.SIZE];
        fillSolution(solution, 0, 0);
        return solution;
    }

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

    /**
     * Create a puzzle on the provided Board by copying the solution then removing
     * cells until only prefilledCount remain.
     *
     * Uniqueness rule: we only keep a removal if the resulting puzzle still has
     * exactly 1 solution (checked via {@link SudokuUtils#countSolutions}).
     */
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
        // SudokuUtils.countSolutions will copy internally, so correctness is unchanged.
        int[][] candidate = new int[Board.SIZE][Board.SIZE];

        for (int[] cell : allCells) {
            if (removed >= toRemove) break;

            int r = cell[0];
            int c = cell[1];
            if (board.get(r, c) == 0) continue;

            // Try removing
            board.clear(r, c);

            // Uniqueness check (early stop after 2 solutions)
            // Copy current board state into the reusable candidate buffer.
            int[][] snapshot = board.toArrayCopy();
            for (int rr = 0; rr < Board.SIZE; rr++) {
                System.arraycopy(snapshot[rr], 0, candidate[rr], 0, Board.SIZE);
            }

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

}

