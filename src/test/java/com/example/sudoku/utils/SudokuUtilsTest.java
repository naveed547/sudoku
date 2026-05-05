package com.example.sudoku.utils;

import com.example.sudoku.Board;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuUtilsTest {

    @Test
    void parseCell_acceptsUpperAndLowercase() {
        int[] a3 = SudokuUtils.parseCell("A3");
        assertNotNull(a3);
        assertEquals(0, a3[0]);
        assertEquals(2, a3[1]);

        int[] b9 = SudokuUtils.parseCell("b9");
        assertNotNull(b9);
        assertEquals(1, b9[0]);
        assertEquals(8, b9[1]);
    }

    @Test
    void parseCell_rejectsInvalid() {
        assertNull(SudokuUtils.parseCell(null));
        assertNull(SudokuUtils.parseCell(""));
        assertNull(SudokuUtils.parseCell("Z1"));
        assertNull(SudokuUtils.parseCell("A0"));
        assertNull(SudokuUtils.parseCell("A10"));
        assertNull(SudokuUtils.parseCell("AA"));
        assertNull(SudokuUtils.parseCell("A3x"));
    }

    @Test
    void isValidMove_rejectsRowDuplicates() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        b[0][0] = 5;
        assertFalse(SudokuUtils.isValidMove(b, 0, 1, 5));
    }

    @Test
    void isValidMove_rejectsColumnDuplicates() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        b[0][0] = 7;
        assertFalse(SudokuUtils.isValidMove(b, 1, 0, 7));
    }

    @Test
    void isValidMove_rejectsBoxDuplicates() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        b[1][1] = 9; // top-left 3x3 box
        assertFalse(SudokuUtils.isValidMove(b, 0, 2, 9));
    }

    @Test
    void isValidMove_allowsNonConflictingPlacement() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        b[0][0] = 5;
        assertTrue(SudokuUtils.isValidMove(b, 0, 1, 3));
    }

    @Test
    void validateWholeBoard_reportsDuplicates() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        b[0][0] = 1;
        b[0][1] = 1; // row duplicate
        b[1][0] = 2;
        b[2][0] = 2; // column duplicate

        List<String> problems = SudokuUtils.validateWholeBoard(b);
        assertTrue(problems.size() >= 2);
        String joined = String.join("\n", problems).toLowerCase();
        assertTrue(joined.contains("row"));
        assertTrue(joined.contains("column"));
    }

    @Test
    void isCompleteAndValid_returnsFalseIfAnyZero() {
        int[][] b = new int[Board.SIZE][Board.SIZE];
        b[0][0] = 1;
        assertFalse(SudokuUtils.isCompleteAndValid(b));
    }
}

