package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.SudokuGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

public class ClearCommandTest {
    private Board board;
    private Random rand;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        rand = new Random(42);
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        board = new Board();
        SudokuGenerator gen = new SudokuGenerator(rand);
        int[][] solution = gen.generateFullSolution();
        gen.createPuzzle(board, solution, 30);
    }

    @Test
    void clearCommand_whenNonPrefilledFilledCell_thenClearsAndShowsMessage() {
        // Given filled non-prefilled cell A1 with value 7
        board.set(0,0, 7);
        board.setPrefilled(0,0, false);
        ClearCommand cmd = new ClearCommand("A1");
        
        // When executed
        cmd.execute(board, null, null);
        
        // Then cell cleared, confirmation message
        assertEquals(0, board.get(0,0));
        assertTrue(outContent.toString().contains("Cleared A1"));
    }

    @Test
    void clearCommand_whenPrefilledCell_thenRejectsAndKeepsValue() {
        // Given prefilled cell A1 with value 7
        board.setPrefilled(0,0, true);
        board.set(0,0, 7);
        ClearCommand cmd = new ClearCommand("A1");
        
        // When executed
        cmd.execute(board, null, null);
        
        // Then value preserved, rejection message
        assertEquals(7, board.get(0,0));
        assertTrue(outContent.toString().contains("Cannot clear"));
    }

    @Test
    void clearCommand_whenAlreadyEmptyCell_thenShowsAlreadyEmpty() {
        // Given empty cell A1
        ClearCommand cmd = new ClearCommand("A1");
        
        // When executed
        cmd.execute(board, null, null);
        
        // Then no change, already empty message
        assertEquals(0, board.get(0,0));
        assertTrue(outContent.toString().contains("already empty"));
    }

    @Test
    void clearCommand_whenInvalidCell_thenShowsError() {
        // Given invalid cell Z9
        ClearCommand cmd = new ClearCommand("Z9");
        
        // When executed
        cmd.execute(board, null, null);
        
        // Then error message
        assertTrue(outContent.toString().contains("Invalid cell"));
    }
}
