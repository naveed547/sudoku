package com.example.sudoku.commands;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuGenerator;

public class CheckCommandTest {
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
    void checkCommand_whenNoDuplicates_thenShowsNoProblems() {
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                board.set(r, c, solution[r][c]);
            }
        }

        CheckCommand cmd = new CheckCommand();
        CommandResult result = cmd.execute(board, null, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("No rule violations detected."));
        assertFalse(result.message.contains("Problems found:"));
    }

    @Test
    void checkCommand_whenRowDuplicate_thenListsProblem() {
        board.set(0, 0, 1);
        board.set(0, 1, 1);

        CheckCommand cmd = new CheckCommand();
        CommandResult result = cmd.execute(board, null, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Problems found:"));
        assertTrue(result.message.toLowerCase().contains("duplicate 1 in row"));
    }

    @Test
    void checkCommand_whenColumnDuplicate_thenListsProblem() {
        board.set(0, 0, 1);
        board.set(1, 0, 1);

        CheckCommand cmd = new CheckCommand();
        CommandResult result = cmd.execute(board, null, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Problems found:"));
        assertTrue(result.message.toLowerCase().contains("duplicate 1 in column"));
    }

    @Test
    void checkCommand_whenBoxDuplicate_thenListsProblem() {
        board.set(0, 0, 1);
        board.set(1, 1, 1);

        CheckCommand cmd = new CheckCommand();
        CommandResult result = cmd.execute(board, null, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Problems found:"));
        assertTrue(result.message.toLowerCase().contains("duplicate 1 in 3x3 box"));
    }

    @Test
    void checkCommand_whenMultipleProblems_thenListsAll() {
        board.set(0, 0, 1);
        board.set(0, 1, 1);
        board.set(1, 0, 1);

        CheckCommand cmd = new CheckCommand();
        CommandResult result = cmd.execute(board, null, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Problems found:"));
        assertTrue(result.message.toLowerCase().contains("duplicate 1 in row"));
        assertTrue(result.message.toLowerCase().contains("duplicate 1 in column"));
    }
}