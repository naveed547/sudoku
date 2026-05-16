package com.example.sudoku;

import com.example.sudoku.utils.SudokuValidator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class SudokuGameTest {

    @Test
    void mainMethodExistsAndHasExpectedSignature() throws Exception {
        Method main = SudokuGame.class.getMethod("main", String[].class);
        assertNotNull(main);
        assertTrue(java.lang.reflect.Modifier.isStatic(main.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(main.getModifiers()));
    }

    @Test
    void sudokuGameCanBeConstructed() {
        assertDoesNotThrow(SudokuGame::new);
    }

    @Test
    void generatedSolutionIsCompleteAndValid() {
        // Smoke test for the core bootstrap path used by SudokuGame:
        // SudokuGame -> Board.generateAndSetPuzzle() -> SudokuGenerator.generateFullSolution()
        Board board = new Board();
        int[][] solution = board.generateAndSetPuzzle();

        assertNotNull(solution);
        assertEquals(Board.SIZE, solution.length);
        assertEquals(Board.SIZE, solution[0].length);

        // The returned solution should be a fully solved valid grid.
        assertTrue(SudokuValidator.isCompleteAndValid(solution));
    }

    @Test
    void validatorRejectsIncompleteSolution() {
        Board board = new Board();
        board.generateAndSetPuzzle();

        // Puzzle grid is intentionally incomplete (has zeros).
        assertFalse(SudokuValidator.isCompleteAndValid(board.toArrayCopy()));
    }
}
