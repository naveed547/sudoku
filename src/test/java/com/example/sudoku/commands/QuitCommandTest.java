package com.example.sudoku.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.sudoku.Board;

public class QuitCommandTest {

    @Test
    void quitCommand_alwaysReturnsFalseToExitGame() {
        QuitCommand cmd = new QuitCommand();
        Board board = new Board();

        CommandResult result = cmd.execute(board, null, null);

        assertFalse(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Quitting"));
    }

    @Test
    void quitCommand_doesNotModifyBoard() {
        Board board = new Board();
        board.set(0, 0, 5);

        QuitCommand cmd = new QuitCommand();
        cmd.execute(board, null, null);

        assertEquals(5, board.get(0, 0));
    }

    @Test
    void quitCommand_independentOfSolutionOrScanner() {
        QuitCommand cmd = new QuitCommand();

        CommandResult result1 = cmd.execute(null, null, null);
        CommandResult result2 = cmd.execute(new Board(), new int[9][9], null);

        assertFalse(result1.success);
        assertFalse(result2.success);
    }
}

