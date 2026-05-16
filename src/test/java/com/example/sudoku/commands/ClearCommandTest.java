package com.example.sudoku.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuGenerator;

public class ClearCommandTest {
    private Board board;
    private Random rand;
    private int[][] solution;

    @BeforeEach
    void setUp() {
        rand = new Random(42);

        board = new Board();
        SudokuGenerator gen = new SudokuGenerator(rand);
        solution = gen.generateFullSolution();
        gen.createPuzzle(board, solution, 30);
    }

    @Test
    void clearCommand_whenNonPrefilledFilledCell_thenClearsAndReturnsMessage() {
        board.set(0, 0, 7);
        board.setPrefilled(0, 0, false);

        ClearCommand cmd = new ClearCommand("A1");
        CommandResult result = cmd.execute(board, null, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Cleared A1"));
        assertEquals(0, board.get(0, 0));
    }

    @Test
    void clearCommand_whenPrefilledCell_thenRejectsAndKeepsValue() {
        board.setPrefilled(0, 0, true);
        board.set(0, 0, 7);

        ClearCommand cmd = new ClearCommand("A1");
        CommandResult result = cmd.execute(board, null, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Cannot clear"));
        assertEquals(7, board.get(0, 0));
    }

    @Test
    void clearCommand_whenAlreadyEmptyCell_thenShowsAlreadyEmpty() {
        ClearCommand cmd = new ClearCommand("A1");
        CommandResult result = cmd.execute(board, null, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.toLowerCase().contains("already empty"));
        assertEquals(0, board.get(0, 0));
    }

    @Test
    void clearCommand_whenInvalidCell_thenShowsError() {
        ClearCommand cmd = new ClearCommand("Z9");
        CommandResult result = cmd.execute(board, null, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Invalid cell"));
    }
}

