package com.example.sudoku;

import com.example.sudoku.utils.SudokuGenerator;
import com.example.sudoku.utils.SudokuValidator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

    @Test
    void run_handlesExceptionDuringCommandExecutionAndContinuesLoop() {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;

        // Mockito-inline is required to use mockStatic for static factory methods
        try (var mockedFactory = mockStatic(com.example.sudoku.commands.CommandFactory.class)) {
            com.example.sudoku.commands.Command throwingCmd = mock(com.example.sudoku.commands.Command.class);
            when(throwingCmd.execute(any())).thenThrow(new RuntimeException("Command execution failed"));
            
            com.example.sudoku.commands.Command quitCmd = new com.example.sudoku.commands.QuitCommand();

            // Setup the factory to return a failing command followed by a quit command
            mockedFactory.when(() -> com.example.sudoku.commands.CommandFactory.parse(eq("fail"), any(), any()))
                    .thenReturn(throwingCmd);
            mockedFactory.when(() -> com.example.sudoku.commands.CommandFactory.parse(eq("quit"), any(), any()))
                    .thenReturn(quitCmd);

            // Provide inputs: first one fails, second one quits
            System.setIn(new ByteArrayInputStream("fail\nquit\n".getBytes()));
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent));

            new SudokuGame().run();

            String output = outContent.toString();
            
            // Verify that the exception was caught and handled
            assertTrue(output.contains("unexpected error occurred: Command execution failed"));
            // Verify that the loop continued and processed the next command
            assertTrue(output.contains("Quitting. Bye!"));
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    @Test
    void board_resetState_wipesAllData() {
        Board board = new Board();
        board.set(0, 0, 5);
        board.setPrefilled(0, 0, true);
        board.consumeHint();
        board.setPuzzleStarted(true);

        board.resetState();

        assertEquals(0, board.get(0, 0));
        assertFalse(board.isPrefilled(0, 0));
        assertEquals(Board.MAX_HINT_ALLOWED, board.getHintCountLeft());
        assertFalse(board.isPuzzleStarted());
    }
}