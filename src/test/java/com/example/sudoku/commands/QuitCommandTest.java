package com.example.sudoku.commands;

import com.example.sudoku.Board;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class QuitCommandTest {
    
    @Test
    void quitCommand_alwaysReturnsFalseToExitGame() {
        // Given QuitCommand
        QuitCommand cmd = new QuitCommand();
        Board board = new Board(); // dummy board
        
        // When executed
        boolean continueGame = cmd.execute(board, null, null);
        
        // Then returns false to exit game loop
        assertFalse(continueGame);
    }

    @Test
    void quitCommand_doesNotModifyBoard() {
        // Given board with some values
        Board board = new Board();
        board.set(0,0, 5);
        
        // When executed
        QuitCommand cmd = new QuitCommand();
        cmd.execute(board, null, null);
        
        // Then board unchanged
        assertEquals(5, board.get(0,0));
    }

    @Test
    void quitCommand_independentOfSolutionOrScanner() {
        // Given any solution/scanner
        QuitCommand cmd = new QuitCommand();
        
        // When executed with any params
        boolean result1 = cmd.execute(null, null, null);
        boolean result2 = cmd.execute(new Board(), new int[9][9], null);
        
        // Then always false
        assertFalse(result1);
        assertFalse(result2);
    }
}
