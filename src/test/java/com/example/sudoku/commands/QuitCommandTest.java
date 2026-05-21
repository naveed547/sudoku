package com.example.sudoku.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.sudoku.Board;

public class QuitCommandTest {

    @Test
    void quitCommand_alwaysReturnsFalseToExitGame() {
        QuitCommand cmd = new QuitCommand();
        Board board = new Board();

        CommandResult result = cmd.execute(board);

        assertFalse(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Quitting"));
    }

    @Test
    void quitCommand_doesNotModifyBoard() {
        Board board = new Board();
        board.set(0, 0, 5);

        QuitCommand cmd = new QuitCommand();
        cmd.execute(board);

        assertEquals(5, board.get(0, 0));
    }
}

