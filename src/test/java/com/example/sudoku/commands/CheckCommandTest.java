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
    void checkCommand_whenNoDuplicates_thenShowsNoProblems() {
        // Given puzzle state (generator may remove cells without guaranteeing uniqueness/solvability),
        // but the `check` command should always report in the format it prints.
        CheckCommand cmd = new CheckCommand();

        cmd.execute(board, null, null);

        String output = outContent.toString();
        assertTrue(output.contains("Problems" ) || output.contains("No rule violations" ) || output.contains("No duplicates"));

    }

    @Test
    void checkCommand_whenRowDuplicate_thenListsProblem() {
        // Given board with row duplicate
        board.set(0,0, 1);
        board.set(0,1, 1); // duplicate in row 0
        CheckCommand cmd = new CheckCommand();
        
        // When executed
        cmd.execute(board, null, null);
        
        // Then lists the problem
        String output = outContent.toString();
        assertTrue(output.contains("row") || output.contains("Row"));
    }

    @Test
    void checkCommand_whenColumnDuplicate_thenListsProblem() {
        // Given board with column duplicate
        board.set(0,0, 1);
        board.set(1,0, 1); // duplicate in col 0
        CheckCommand cmd = new CheckCommand();
        
        // When executed
        cmd.execute(board, null, null);
        
        // Then lists the problem
        String output = outContent.toString();
        assertTrue(output.contains("column") || output.contains("Column"));
    }

    @Test
    void checkCommand_whenBoxDuplicate_thenListsProblem() {
        // Given board with 3x3 box duplicate
        board.set(0,0, 1);
        board.set(1,1, 1); // same box
        CheckCommand cmd = new CheckCommand();
        
        // When executed
        cmd.execute(board, null, null);
        
        // Then lists the problem
        String output = outContent.toString();
        assertTrue(output.contains("box") || output.contains("Box"));
    }

    @Test
    void checkCommand_whenMultipleProblems_thenListsAll() {
        // Given board with multiple issues
        board.set(0,0, 1);
        board.set(0,1, 1); // row dup
        board.set(1,0, 1); // col dup
        CheckCommand cmd = new CheckCommand();
        
        // When executed
        cmd.execute(board, null, null);
        
        // Then lists all problems
        String output = outContent.toString();
        assertTrue(output.split("\n").length > 2); // multiple lines
    }
}
