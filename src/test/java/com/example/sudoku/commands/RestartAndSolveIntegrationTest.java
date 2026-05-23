package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.GameService;
import com.example.sudoku.utils.SudokuGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

/**
 * Integration test to verify the interaction between Restart and Solve commands.
 */
public class RestartAndSolveIntegrationTest {

    private Board board;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        // Use a fixed seed for deterministic behavior during tests
        gameService = new GameService(new SudokuGenerator(new Random(42)));
        board = gameService.startNewGame();
    }

    @Test
    void testRestartClearsUserMovesButPreservesPuzzle() {
        // 1. Identify a cell that is not prefilled and place a value
        int r = 0, c = 0;
        while (board.isPrefilled(r, c)) {
            c++;
            if (c == 9) { c = 0; r++; }
        }

        board.placeValue(r, c, 5);
        assertEquals(5, board.get(r, c), "Value should be placed");
        assertTrue(board.isPuzzleStarted(), "Puzzle should be marked as started");

        // 2. Execute Restart
        Command restart = new RestartCommand();
        restart.execute(board);

        // 3. Verify state
        assertEquals(0, board.get(r, c), "User move should be cleared after restart");
        assertFalse(board.isPuzzleStarted(), "puzzleStarted flag should be reset");
        assertEquals(Board.MAX_HINT_ALLOWED, board.getHintCountLeft(), "Hints should be reset");
    }

    @Test
    void testSolveAfterRestartProducesValidBoard() {
        // 1. Restart to ensure clean state
        new RestartCommand().execute(board);

        // 2. Execute Solve
        new SolveCommand().execute(board);

        // 3. Verify completion
        assertTrue(board.isSolved(), "Board should be solved after SolveCommand");
        int[][] solution = board.getSolution();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assertEquals(solution[i][j], board.get(i, j), "Board must match stored solution");
            }
        }
    }
}