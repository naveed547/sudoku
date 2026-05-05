package com.example.sudoku.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
import com.example.sudoku.commands.PlaceCommand;
import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuGenerator;


public class PlaceCommandTest {
    private Board board;
    private int[][] solution;
    private Random rand;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        rand = new Random(42);
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        board = new Board();
        SudokuGenerator gen = new SudokuGenerator(rand);
        solution = gen.generateFullSolution();
        gen.createPuzzle(board, solution, 30);
    }

    @Test
    void placeCommand_whenValidEmptyCell_thenUpdatesBoardAndShowsMessage() {
        // Given valid empty non-prefilled cell A1 and number 5
        PlaceCommand cmd = new PlaceCommand("A1", "5");
        
        // When command executed
        boolean continueGame = cmd.execute(board, solution, null);
        
        // Then board updated at A1=5, continue game, confirmation message
        assertTrue(continueGame);
        assertEquals(5, board.get(0, 0));
        assertTrue(outContent.toString().contains("Placed 5 at A1"));
    }

    @Test
    void placeCommand_whenPrefilledCell_thenRejectsWithoutChange() {
        // Given prefilled cell at A1
        board.setPrefilled(0, 0, true);
        PlaceCommand cmd = new PlaceCommand("A1", "5");
        
        // When executed
        cmd.execute(board, solution, null);
        
        // Then no change to board, rejection message
        assertEquals(0, board.get(0, 0));
        assertTrue(outContent.toString().contains("Cannot modify"));
    }

    @Test
    void placeCommand_whenInvalidCellFormat_thenShowsError() {
        // Given invalid cell J10
        PlaceCommand cmd = new PlaceCommand("J10", "5");
        
        // When executed
        cmd.execute(board, solution, null);
        
        // Then error message
        assertTrue(outContent.toString().contains("Invalid cell"));
    }

    @Test
    void placeCommand_whenInvalidNumber_thenRejects() {
        // Given number 0
        PlaceCommand cmd = new PlaceCommand("A1", "0");
        
        // When executed
        cmd.execute(board, solution, null);
        
        // Then rejection
        assertTrue(outContent.toString().contains("1 and 9"));
        assertEquals(0, board.get(0, 0));
    }

    @Test
    void placeCommand_whenDuplicateMove_thenAcceptsButCheckWillReport() {
        // Given same number already in row
        board.set(0, 1, 5);
        PlaceCommand cmd = new PlaceCommand("A1", "5");

        // When executed
        boolean continueGame = cmd.execute(board, solution, null);

        // Then move is accepted; check is responsible for reporting violations
        assertTrue(continueGame);
        assertEquals(5, board.get(0, 0));
        assertTrue(outContent.toString().contains("Placed 5 at A1"));
    }

}
