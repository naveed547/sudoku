package com.example.sudoku.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

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
    void placeCommand_whenValidEmptyCell_thenUpdatesBoardAndReturnsSuccessMessage() {
        PlaceCommand cmd = new PlaceCommand("A1", "5");
        CommandResult result = cmd.execute(board);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Placed 5 at A1"));
        assertEquals(5, board.get(0, 0));
    }

    @Test
    void placeCommand_whenPrefilledCell_thenRejectsWithoutChange() {
        board.setPrefilled(0, 0, true);
        PlaceCommand cmd = new PlaceCommand("A1", "5");

        CommandResult result = cmd.execute(board);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Cannot modify"));
        assertEquals(0, board.get(0, 0));
    }

    @Test
    void placeCommand_whenInvalidCellFormat_thenReturnsErrorMessage() {
        PlaceCommand cmd = new PlaceCommand("J10", "5");

        CommandResult result = cmd.execute(board);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Invalid cell reference"));
    }

    @Test
    void placeCommand_whenInvalidNumber_thenRejects() {
        PlaceCommand cmd = new PlaceCommand("A1", "0");

        CommandResult result = cmd.execute(board);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Number must be between 1 and 9"));
        assertEquals(0, board.get(0, 0));
    }

    @Test
    void placeCommand_whenDuplicateMove_thenAcceptsButCheckWillReport() {
        board.set(0, 1, 5);
        PlaceCommand cmd = new PlaceCommand("A1", "5");

        CommandResult result = cmd.execute(board);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Placed 5 at A1"));
        assertEquals(5, board.get(0, 0));
    }
}

