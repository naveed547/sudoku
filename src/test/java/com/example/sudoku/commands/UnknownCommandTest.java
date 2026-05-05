package com.example.sudoku.commands;

import com.example.sudoku.Board;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

public class UnknownCommandTest {
    
    @Test
    void unknownCommand_showsErrorMessage() {
        // Given
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        UnknownCommand cmd = new UnknownCommand();
        Board board = new Board();
        
        // When executed
        cmd.execute(board, null, null);
        
        // Then shows unknown command message
        String output = outContent.toString();
        assertTrue(output.contains("Unknown") || output.contains("Invalid"));
        assertTrue(output.contains("format") || output.contains("command"));
    }

    @Test
    void unknownCommand_continuesGame() {
        // Given
        UnknownCommand cmd = new UnknownCommand();
        Board board = new Board();
        
        // When executed
        boolean continueGame = cmd.execute(board, null, null);
        
        // Then continues game (does not exit)
        assertTrue(continueGame);
    }

    @Test
    void unknownCommand_safeForAllInputs() {
        // Given various nulls
        UnknownCommand cmd = new UnknownCommand();
        
        // When executed with nulls
        boolean result = cmd.execute(null, null, null);
        
        // Then safe, continues
        assertTrue(result);
    }
}
