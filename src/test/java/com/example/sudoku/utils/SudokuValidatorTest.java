package com.example.sudoku.utils;

import com.example.sudoku.Board;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuValidatorTest {

    @Test
    void parseCell_acceptsUpperAndLowercase() {
        int[] a3 = SudokuValidator.parseCell("A3");
        assertNotNull(a3);
        assertEquals(0, a3[0]);
        assertEquals(2, a3[1]);

        int[] b9 = SudokuValidator.parseCell("b9");
        assertNotNull(b9);
        assertEquals(1, b9[0]);
        assertEquals(8, b9[1]);
    }

    @Test
    void parseCell_acceptsLeadingAndTrailingWhitespace() {
        int[] a3 = SudokuValidator.parseCell("  a3  ");
        assertNotNull(a3);
        assertEquals(0, a3[0]);
        assertEquals(2, a3[1]);

        int[] c1 = SudokuValidator.parseCell("\nC1\t");
        assertNotNull(c1);
        assertEquals(2, c1[0]);
        assertEquals(0, c1[1]);
    }

    @Test
    void parseCell_rejectsInvalid() {
        assertNull(SudokuValidator.parseCell(null));
        assertNull(SudokuValidator.parseCell(""));
        assertNull(SudokuValidator.parseCell("Z1"));
        assertNull(SudokuValidator.parseCell("A0"));
        assertNull(SudokuValidator.parseCell("A10"));
        assertNull(SudokuValidator.parseCell("AA"));
        assertNull(SudokuValidator.parseCell("A3x"));
    }

    @Test
    void parseCell_handlesInternalWhitespace_behavior() {
        // Current implementation does: trim() + upper + substring(1) + Integer.parseInt(colPart).
        // Despite the above, "A 3" is expected to be rejected by current behavior.
        assertNull(SudokuValidator.parseCell("A 3"));
    }

    @Test
    void isValidMove_rejectsRowDuplicates() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        b[0][0] = 5;
        assertFalse(SudokuValidator.isValidMove(b, 0, 1, 5));
    }

    @Test
    void isValidMove_rejectsColumnDuplicates() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        b[0][0] = 7;
        assertFalse(SudokuValidator.isValidMove(b, 1, 0, 7));
    }

    @Test
    void isValidMove_rejectsBoxDuplicates() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        b[1][1] = 9; // top-left 3x3 box
        assertFalse(SudokuValidator.isValidMove(b, 0, 2, 9));
    }

    @Test
    void isValidMove_allowsNonConflictingPlacement() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        b[0][0] = 5;
        assertTrue(SudokuValidator.isValidMove(b, 0, 1, 3));
    }

    @Test
    void validateWholeBoard_reportsBoxDuplicates() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        // top-left 3x3 box (rows 0..2, cols 0..2)
        b[0][0] = 9;
        b[1][1] = 9; // box duplicate

        List<String> problems = SudokuValidator.validateWholeBoard(b);
        String joined = String.join("\n", problems).toLowerCase();
        assertTrue(joined.contains("box"), "Expected a box-duplicate problem");
    }

    @Test
    void validateWholeBoard_reportsDuplicates() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        b[0][0] = 1;
        b[0][1] = 1; // row duplicate
        b[1][0] = 2;
        b[2][0] = 2; // column duplicate

        List<String> problems = SudokuValidator.validateWholeBoard(b);
        assertTrue(problems.size() >= 2);
        String joined = String.join("\n", problems).toLowerCase();
        assertTrue(joined.contains("row"));
        assertTrue(joined.contains("column"));
    }

    @Test
    void isCompleteAndValid_returnsTrueForSolvedBoard() {
        SudokuGenerator gen = new SudokuGenerator(new java.util.Random(42));
        int[][] solution = gen.generateFullSolution();

        assertTrue(SudokuValidator.isCompleteAndValid(solution), "Solved solution must be complete and valid");
    }

    @Test
    void countSolutions_returnsZeroForConflictingBoard() {
        // Use a fully filled (solved) board and introduce a direct conflict.
        // Since there are no zeros, countSolutions will validate immediately (no expensive backtracking).
        SudokuGenerator gen = new SudokuGenerator(new java.util.Random(42));
        int[][] solution = gen.generateFullSolution();

        // Introduce a row duplicate by corrupting one cell.
        solution[0][1] = solution[0][0];

        int solutions = SudokuValidator.countSolutions(solution, 2);
        assertEquals(0, solutions);
    }

    @Test
    void countSolutions_returnsOneForValidSolvedBoard() {
        SudokuGenerator gen = new SudokuGenerator(new java.util.Random(42));
        int[][] solution = gen.generateFullSolution();

        int solutions = SudokuValidator.countSolutions(solution, 2);
        assertEquals(1, solutions);
    }

    @Test
    void countSolutions_respectsLimit() {
        SudokuGenerator gen = new SudokuGenerator(new java.util.Random(42));
        int[][] solution = gen.generateFullSolution();

        // Use limit=1; should still find at least 1 solution and stop early.
        int solutions = SudokuValidator.countSolutions(solution, 1);
        assertEquals(1, solutions);
    }

    @Test
    void isCompleteAndValid_returnsFalseIfAnyZero() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        b[0][0] = 1;
        assertFalse(SudokuValidator.isCompleteAndValid(b));
    }

    @Test
    void validateWholeBoard_marksInvalidPuzzleForConflictingFilledBoard() {
        // Create an invalid *complete* board by introducing a row duplicate.
        SudokuGenerator gen = new SudokuGenerator(new java.util.Random(42));
        int[][] solution = gen.generateFullSolution();

        // Make the board invalid: duplicate value within a row.
        solution[0][1] = solution[0][0];

        List<String> problems = SudokuValidator.validateWholeBoard(solution);
        assertFalse(problems.isEmpty(), "Expected problems for a conflicting filled board");
        assertEquals(0, SudokuValidator.countSolutions(solution, 2));
    }

    @Test
    void countSolutions_returnsZero_forUnsolvablePartialPuzzle() {
        // Start from a valid solution, then corrupt one filled value and keep some cells empty.
        // This should yield 0 solutions (unsolvable puzzle).
        SudokuGenerator gen = new SudokuGenerator(new java.util.Random(42));
        int[][] solution = gen.generateFullSolution();

        // Corrupt a cell to make the puzzle contradictory.
        // Keep at least one other cell empty so it is "unsolvable puzzle" (not just invalid complete board).
        int[][] partial = new int[Board.SIZE][Board.SIZE];
        for (int r = 0; r < Board.SIZE; r++) {
            System.arraycopy(solution[r], 0, partial[r], 0, Board.SIZE);
        }

        // Set two cells to break constraints but keep zeros to force solver/backtracking.
        partial[0][1] = ((partial[0][1] % Board.SIZE) + 1); // different value, likely invalid vs row/box
        partial[0][2] = 0; // at least one empty cell

        int solutions = SudokuValidator.countSolutions(partial, 2);
        assertEquals(0, solutions);
    }

    @Test
    void countSolutions_returnsAtLeastTwo_forMultipleSolutions_emptyBoard() {
        // Empty board has multiple solutions.
        // countSolutions should stop at limit and return >= limit.
        int[][] empty = new int[Board.SIZE][Board.SIZE];

        int solutions = SudokuValidator.countSolutions(empty, 2);
        assertTrue(solutions >= 2, "Expected early-stop at limit=2 for an empty board");
    }

    @Test
    void countSolutions_limitOne_returnsOne() {
        int[][] empty = new int[Board.SIZE][Board.SIZE];
        int solutions = SudokuValidator.countSolutions(empty, 1);
        assertEquals(1, solutions, "limit=1 should stop after finding the first solution");
    }

    @Test
    void validateWholeBoard_reportsColumnDuplicates() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        // column 0 duplicate: (row 0,col 0) and (row 1,col 0)
        b[0][0] = 4;
        b[1][0] = 4;

        List<String> problems = SudokuValidator.validateWholeBoard(b);
        assertFalse(problems.isEmpty(), "Expected problems for a conflicting filled board");
        String joined = String.join("\n", problems).toLowerCase();
        assertTrue(joined.contains("column"), "Expected a column-duplicate problem");
    }

    @Test
    void validateWholeBoard_reportsBoxDuplicates_topLeftBox() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        // top-left 3x3 box duplicate: (0,0) and (2,2)
        b[0][0] = 7;
        b[2][2] = 7;

        List<String> problems = SudokuValidator.validateWholeBoard(b);
        assertFalse(problems.isEmpty(), "Expected problems for a conflicting filled board");
        String joined = String.join("\n", problems).toLowerCase();
        assertTrue(joined.contains("box"), "Expected a box-duplicate problem");
    }

    @Test
    void solvedBoard_contracts_validateAndCountsAgree() {
        SudokuGenerator gen = new SudokuGenerator(new java.util.Random(42));
        int[][] solution = gen.generateFullSolution();

        assertTrue(SudokuValidator.isCompleteAndValid(solution), "Solved solution must be complete and valid");
        assertEquals(1, SudokuValidator.countSolutions(solution, 2), "Solved board must have exactly 1 solution");
        assertTrue(SudokuValidator.validateWholeBoard(solution).isEmpty(), "Solved board must have no validation problems");
    }

    @Test
    void countSolutions_doesNotMutateInput() {
        SudokuGenerator gen = new SudokuGenerator(new java.util.Random(42));
        int[][] solution = gen.generateFullSolution();

        // Make it a partial puzzle by emptying a few cells.
        int[][] original = new int[Board.SIZE][Board.SIZE];
        for (int r = 0; r < Board.SIZE; r++) {
            System.arraycopy(solution[r], 0, original[r], 0, Board.SIZE);
        }
        original[0][0] = 0;
        original[4][4] = 0;
        original[8][8] = 0;

        // Clone "before"
        int[][] before = new int[Board.SIZE][Board.SIZE];
        for (int r = 0; r < Board.SIZE; r++) {
            System.arraycopy(original[r], 0, before[r], 0, Board.SIZE);
        }

        SudokuValidator.countSolutions(original, 2);

        // Assert input unchanged
        for (int r = 0; r < Board.SIZE; r++) {
            assertArrayEquals(before[r], original[r], "countSolutions should not mutate the input board");
        }
    }
}

