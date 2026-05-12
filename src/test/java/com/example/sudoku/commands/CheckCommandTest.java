package com.example.sudoku.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;
import com.example.sudoku.commands.CheckCommand;
import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuGenerator;


public class CheckCommandTest {
    private Board board;
    private Random rand;
    private ByteArrayOutputStream outContent;
    private int[][] solution;

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
    void checkCommand_whenNoDuplicates_thenShowsNoProblems() {
        // Given a fully valid solved board (no duplicates possible)
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                board.set(r, c, solution[r][c]);
            }
        }

        CheckCommand cmd = new CheckCommand();
        cmd.execute(board, null, null);

        String output = outContent.toString();
        assertTrue(output.contains("No rule violations detected."), "Expected success message when there are no violations");
        assertFalse(output.contains("Problems found:"), "Should not list problems when solved board is valid");
    }

    @Test
    void checkCommand_whenRowDuplicate_thenListsProblem() {
        // Given board with row duplicate
        board.set(0, 0, 1);
        board.set(0, 1, 1); // duplicate in row 0

        CheckCommand cmd = new CheckCommand();
        cmd.execute(board, null, null);

        String output = outContent.toString();
        assertTrue(output.contains("Problems found:"), "Expected problems header");
        assertTrue(output.toLowerCase().contains("duplicate 1 in row"), "Expected row duplicate to be reported");
    }

    @Test
    void checkCommand_whenColumnDuplicate_thenListsProblem() {
        // Given board with column duplicate
        board.set(0, 0, 1);
        board.set(1, 0, 1); // duplicate in col 0

        CheckCommand cmd = new CheckCommand();
        cmd.execute(board, null, null);

        String output = outContent.toString();
        assertTrue(output.contains("Problems found:"), "Expected problems header");
        assertTrue(output.toLowerCase().contains("duplicate 1 in column"), "Expected column duplicate to be reported");
    }

    @Test
    void checkCommand_whenBoxDuplicate_thenListsProblem() {
        // Given board with 3x3 box duplicate
        board.set(0, 0, 1);
        board.set(1, 1, 1); // same box

        CheckCommand cmd = new CheckCommand();
        cmd.execute(board, null, null);

        String output = outContent.toString();
        assertTrue(output.contains("Problems found:"), "Expected problems header");
        assertTrue(output.toLowerCase().contains("duplicate 1 in 3x3 box"), "Expected box duplicate to be reported");
    }

    @Test
    void checkCommand_whenMultipleProblems_thenListsAll() {
        // Given board with multiple issues (row + column)
        board.set(0, 0, 1);
        board.set(0, 1, 1); // row dup
        board.set(1, 0, 1); // col dup

        CheckCommand cmd = new CheckCommand();
        cmd.execute(board, null, null);

        String output = outContent.toString();
        assertTrue(output.contains("Problems found:"), "Expected problems header");
        assertTrue(output.toLowerCase().contains("duplicate 1 in row"), "Expected row duplicate to be reported");
        assertTrue(output.toLowerCase().contains("duplicate 1 in column"), "Expected column duplicate to be reported");
    }
}
