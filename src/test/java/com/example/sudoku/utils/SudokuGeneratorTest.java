package com.example.sudoku.utils;

import com.example.sudoku.Board;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuGeneratorTest {

    @Test
    void generateFullSolution_createsNonZeroValidGrid() {
        SudokuGenerator gen = new SudokuGenerator(new Random(42));
        int[][] solution = gen.generateFullSolution();

        assertNotNull(solution);
        assertEquals(Board.SIZE, solution.length);
        for (int r = 0; r < Board.SIZE; r++) {
            assertEquals(Board.SIZE, solution[r].length);
            for (int c = 0; c < Board.SIZE; c++) {
                assertTrue(solution[r][c] >= 1 && solution[r][c] <= 9);
            }
        }

        assertTrue(SudokuUtils.isCompleteAndValid(solution), "Generated solution must be complete and valid");
    }

    @Test
    void createPuzzle_marksExactlyPrefilledCount() {
        Random rand = new Random(123);
        SudokuGenerator gen = new SudokuGenerator(rand);

        int[][] solution = gen.generateFullSolution();

        Board board = new Board();
        int prefilledCount = 30;
        gen.createPuzzle(board, solution, prefilledCount);

        int prefCount = 0;
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (board.isPrefilled(r, c)) prefCount++;
            }
        }

        assertEquals(prefilledCount, prefCount, "Puzzle must mark the requested number of prefilled cells");

        // sanity: all prefilled cells must match the solution
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (board.isPrefilled(r, c)) {
                    assertEquals(solution[r][c], board.get(r, c));
                }
            }
        }
    }
}

