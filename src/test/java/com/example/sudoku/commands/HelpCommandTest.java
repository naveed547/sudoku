package com.example.sudoku.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.sudoku.Board;

public class HelpCommandTest {
    @Test
    void helpCommand_displaysAllAvailableCommands() {
        HelpCommand cmd = new HelpCommand();
        Board board = new Board();

        CommandResult result = cmd.execute(board);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Commands:"));
        assertTrue(result.message.contains("A3 5"));
        assertTrue(result.message.contains("A3 clear"));
        assertTrue(result.message.contains("hint     - Show random hint (Max 5)"));
        assertTrue(result.message.contains("check"));
        assertTrue(result.message.contains("restart"));
        assertTrue(result.message.contains("solve"));
        assertTrue(result.message.contains("quit"));
        assertTrue(result.message.contains("help"));
    }

    @Test
    void helpCommand_alwaysContinuesGame() {
        HelpCommand cmd = new HelpCommand();
        Board board = new Board();

        CommandResult result = cmd.execute(board);

        assertTrue(result.success);
    }
}
