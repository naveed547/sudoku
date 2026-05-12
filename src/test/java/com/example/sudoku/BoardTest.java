package com.example.sudoku;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;


public class BoardTest {
    @Test
    void setGetAndClearWorks() {
        Board b = new Board();
        assertEquals(0, b.get(0,0));
        b.set(0,0,7);
        assertEquals(7, b.get(0,0));
        b.clear(0,0);
        assertEquals(0, b.get(0,0));
    }

    @Test
    void generateAndSetPuzzle_twice_doesNotLeakPrefilledState() {
        Board b = new Board();

        int[][] solution1 = b.generateAndSetPuzzle();
        int prefCount1 = 0;
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (b.isPrefilled(r, c)) {
                    prefCount1++;
                    assertNotEquals(0, b.get(r, c), "Prefilled cells must have non-zero values after puzzle generation");
                }
            }
        }
        assertEquals(Board.PREFILLED_COUNT, prefCount1, "First puzzle should mark requested number of prefilled cells");

        int[][] solution2 = b.generateAndSetPuzzle();
        int prefCount2 = 0;
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (b.isPrefilled(r, c)) {
                    prefCount2++;
                    assertNotEquals(0, b.get(r, c), "Prefilled cells must have non-zero values after second puzzle generation");
                }
            }
        }
        assertEquals(Board.PREFILLED_COUNT, prefCount2, "Second puzzle should re-mark requested number of prefilled cells");

        // Sanity: ensure we still have a full solution matrix returned.
        assertEquals(9, solution1.length);
        assertEquals(9, solution2.length);
    }

    @Test
    void prefilledMarkingAndQuery() {
        Board b = new Board();
        b.set(0,0,5);
        b.set(1,1,3);
        b.markPrefilledFromBoard();
        assertTrue(b.isPrefilled(0,0));
        assertTrue(b.isPrefilled(1,1));
        assertFalse(b.isPrefilled(2,2));
    }

    @Test
    void getEmptyNonPrefilledCells_returnsOnlyNonPrefilledEmpties() {
        Board b = new Board();
        b.set(0,0,1);
        b.markPrefilledFromBoard();
        // place a non-prefilled value
        b.set(0,1,2);
        // clear one cell
        b.clear(1,1);
        List<int[]> empties = b.getEmptyNonPrefilledCells();
        // ensure (1,1) appears
        boolean found = false;
        for (int[] rc : empties) if (rc[0] == 1 && rc[1] == 1) found = true;
        assertTrue(found);
        // ensure (0,0) not listed
        for (int[] rc : empties) assertFalse(rc[0] == 0 && rc[1] == 0);
    }
}
