package com.example.sudoku.commands;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuGenerator;

public class RestartCommandTest {
    private Board board;
    private Random rand;
    private int[][] solution;

    @BeforeEach
    void setUp() {
        rand = new Random(42);
        board = new Board();
        SudokuGenerator gen = new SudokuGenerator(rand);
        solution = gen.generateFullSolution();
        gen.createPuzzle(board, solution, 30);
    }

    @Test
    void restartCommand_whenUserEntersRestart_resetCurrentBoard() {
        // 1. Identify a user-modifiable cell and a prefilled cell for testing
        int userR = -1, userC = -1;
        int prefR = -1, prefC = -1;

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (board.isPrefilled(r, c)) {
                    prefR = r; prefC = c;
                } else if (userR == -1) {
                    userR = r; userC = c;
                }
            }
        }

        int originalPrefValue = board.get(prefR, prefC);
        assertTrue(originalPrefValue != 0, "Prefilled cell should have a value");

        // 2. Perform actions: place a user value and consume a hint
        board.placeValue(userR, userC, 5);
        board.consumeHint();
        
        assertEquals(5, board.get(userR, userC));
        assertEquals(Board.MAX_HINT_ALLOWED - 1, board.getHintCountLeft());
        assertTrue(board.isPuzzleStarted(), "Flag should be true after a move");

        // 3. Execute restart
        RestartCommand cmd = new RestartCommand();
        CommandResult result = cmd.execute(board);

        // 4. Verify full state recovery
        assertTrue(result.success);
        assertTrue(result.message.contains("Game restarted"));
        
        // Prefilled cell remains intact
        assertEquals(originalPrefValue, board.get(prefR, prefC), "Prefilled cell value must be preserved");
        
        // User cell is cleared
        assertEquals(0, board.get(userR, userC), "User-entered value must be cleared");
        
        // Hints are reset to the maximum allowed
        assertEquals(Board.MAX_HINT_ALLOWED, board.getHintCountLeft(), "Hint count must be reset");
        
        // Header state reset (so the renderer shows "Here is your puzzle:" instead of "Current grid:")
        assertFalse(board.isPuzzleStarted(), "Puzzle started flag should be reset");
    }
}