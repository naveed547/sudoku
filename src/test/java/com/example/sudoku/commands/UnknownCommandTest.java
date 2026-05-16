package com.example.sudoku.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.sudoku.Board;

public class UnknownCommandTest {

    @Test
    void unknownCommand_showsErrorMessage() {
        UnknownCommand cmd = new UnknownCommand();
        Board board = new Board();

        CommandResult result = cmd.execute(board, null, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Unknown") || result.message.contains("Invalid"));
    }

    @Test
    void unknownCommand_continuesGame() {
        UnknownCommand cmd = new UnknownCommand();
        Board board = new Board();

        CommandResult result = cmd.execute(board, null, null);

        assertTrue(result.success);
    }

    @Test
    void unknownCommand_safeForAllInputs() {
        UnknownCommand cmd = new UnknownCommand();

        CommandResult result = cmd.execute(null, null, null);

        assertTrue(result.success);
    }
}

