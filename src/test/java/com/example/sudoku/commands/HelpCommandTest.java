package com.example.sudoku.commands;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;
import com.example.sudoku.Board;


public class HelpCommandTest {
    private ByteArrayOutputStream outContent;

    @Test
    void helpCommand_displaysAllAvailableCommands() {
        // Given
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        HelpCommand cmd = new HelpCommand();
        Board board = new Board(); // dummy
        
        // When executed
        cmd.execute(board, null, null);
        
        // Then comprehensive help text shown
        String output = outContent.toString();
        assertTrue(output.contains("Commands:"));
        assertTrue(output.contains("A3 5"));
        assertTrue(output.contains("A3 clear"));
        assertTrue(output.contains("hint"));
        assertTrue(output.contains("check"));
        assertTrue(output.contains("quit"));
        assertTrue(output.contains("help"));
    }

    @Test
    void helpCommand_alwaysContinuesGame() {
        // Given
        HelpCommand cmd = new HelpCommand();
        Board board = new Board();
        
        // When executed
        boolean continueGame = cmd.execute(board, null, null);
        
        // Then continues game loop
        assertTrue(continueGame);
    }
}
