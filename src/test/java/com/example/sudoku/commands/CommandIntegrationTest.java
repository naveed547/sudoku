package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.GameService;
import com.example.sudoku.utils.SudokuGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CommandIntegrationTest {
    private Board board;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        // Use a fixed seed for deterministic integration testing
        gameService = new GameService(new SudokuGenerator(new Random(42)));
        board = gameService.startNewGame();
    }

    @Test
    void testFullGameCommandCycle() {
        // 1. Verify initial state
        assertFalse(board.isSolved(), "New board should not be solved");
        assertEquals(Board.MAX_HINT_ALLOWED, board.getHintCountLeft());

        // 2. Help Command - Verify message content
        Command help = CommandFactory.parse("help", board, gameService);
        CommandResult helpRes = help.execute(board);
        assertTrue(helpRes.success);
        assertTrue(helpRes.message.contains("Commands:"), "Help should list commands");

        // 3. Invalid Place - Out of bounds
        Command invalidPlace = CommandFactory.parse("Z1 5", board, gameService);
        CommandResult invRes = invalidPlace.execute(board);
        assertTrue(invRes.message.contains("Invalid cell reference"));

        // 4. Valid Place - Find a non-prefilled cell to play in
        int r = -1, c = -1;
        outer: for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!board.isPrefilled(i, j)) {
                    r = i; c = j;
                    break outer;
                }
            }
        }
        String cellCoords = (char) ('A' + r) + "" + (c + 1);
        Command place = CommandFactory.parse(cellCoords + " 5", board, gameService);
        CommandResult placeRes = place.execute(board);
        assertTrue(placeRes.message.contains("Placed 5 at " + cellCoords.toUpperCase()));
        assertEquals(5, board.get(r, c));
        assertTrue(board.isPuzzleStarted());

        // 5. Check Command - No crash, returns validation/grid output
        Command check = CommandFactory.parse("check", board, gameService);
        CommandResult checkRes = check.execute(board);
        assertTrue(checkRes.success);
        assertTrue(checkRes.message.contains("Here is your puzzle:") || checkRes.message.contains("Current grid:"));

        // 6. Hint Command - Consumes a hint and fills a cell
        int hintsBefore = board.getHintCountLeft();
        Command hint = CommandFactory.parse("hint", board, gameService);
        CommandResult hintRes = hint.execute(board);
        assertTrue(hintRes.message.contains("Hint") && hintRes.message.contains("cell"));
        assertEquals(hintsBefore - 1, board.getHintCountLeft());

        // 7. Clear Command - Clears the manual entry from step 4
        Command clear = CommandFactory.parse(cellCoords + " clear", board, gameService);
        CommandResult clearRes = clear.execute(board);
        assertTrue(clearRes.message.contains("Cleared " + cellCoords.toUpperCase()));
        assertEquals(0, board.get(r, c));

        // 8. Restart Command - Resets moves and hints
        Command restart = CommandFactory.parse("restart", board, gameService);
        CommandResult restartRes = restart.execute(board);
        assertTrue(restartRes.message.contains("Game restarted"));
        assertEquals(Board.MAX_HINT_ALLOWED, board.getHintCountLeft());
        assertFalse(board.isPuzzleStarted());

        // 9. Solve Command - Completes the puzzle using stored solution
        Command solve = CommandFactory.parse("solve", board, gameService);
        CommandResult solveRes = solve.execute(board);
        assertTrue(solveRes.message.contains("Solved the puzzle"));
        assertTrue(board.isSolved());

        // 10. Solved State Interceptor - Typing random stuff when solved prompts to ENTER/Quit
        Command blocked = CommandFactory.parse("B2 9", board, gameService);
        CommandResult blockedRes = blocked.execute(board);
        assertTrue(blockedRes.message.contains("Puzzle solved!"));

        // 11. New Game transition - Pressing ENTER (empty string) on solved board restarts
        Command refresh = CommandFactory.parse("", board, gameService);
        CommandResult refreshRes = refresh.execute(board);
        assertTrue(refreshRes.message.contains("Welcome to Sudoku!"));
        assertFalse(board.isSolved(), "Board should be a new unsolved puzzle");

        // 12. Quit Command - Signals the game loop to stop
        Command quit = CommandFactory.parse("quit", board, gameService);
        CommandResult quitRes = quit.execute(board);
        assertFalse(quitRes.success, "Quit command should return success=false to exit loop");
    }
}