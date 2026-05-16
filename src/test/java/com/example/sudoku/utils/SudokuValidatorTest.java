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
}

