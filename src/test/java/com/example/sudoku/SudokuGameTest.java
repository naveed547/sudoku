package com.example.sudoku;

import com.example.sudoku.utils.SudokuGenerator;
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
    void gameServiceStartNewGame_puzzleIsIncomplete() {
        GameService service = new GameService(new SudokuGenerator(new java.util.Random(1)));
        Board board = service.startNewGame();

        // Puzzle grid is intentionally incomplete (has zeros).
        assertFalse(SudokuValidator.isCompleteAndValid(board.toArrayCopy()));
    }

    @Test
    void gameServiceStartNewGame_solutionIsCompleteAndValid() {
        GameService service = new GameService(new SudokuGenerator(new java.util.Random(1)));
        Board board = service.startNewGame();

        // Solution should be a complete valid grid.
        assertNotNull(board.getSolution());
        assertTrue(SudokuValidator.isCompleteAndValid(board.getSolution()));
    }

    @Test
    void board_placeValue_onNonPrefilledCell_succeeds() {
        GameService service = new GameService(new SudokuGenerator(new java.util.Random(1)));
        Board board = service.startNewGame();

        // Place a value on a non-prefilled cell should succeed.
        boolean result = board.placeValue(4, 4, 5);
        assertTrue(result);
        assertEquals(5, board.get(4, 4));
    }

    @Test
    void board_placeValue_rejectedForPrefilledCell() {
        GameService service = new GameService(new SudokuGenerator(new java.util.Random(1)));
        Board board = service.startNewGame();

        // Find the first prefilled cell and try to place over it.
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board.isPrefilled(r, c)) {
                    boolean result = board.placeValue(r, c, 5);
                    assertFalse(result);
                    return;
                }
            }
        }
    }

    @Test
    void board_isSolved_isFalseForFreshPuzzle() {
        GameService service = new GameService(new SudokuGenerator(new java.util.Random(1)));
        Board board = service.startNewGame();

        // Fresh puzzle should not be solved.
        assertFalse(board.isSolved());
    }
}