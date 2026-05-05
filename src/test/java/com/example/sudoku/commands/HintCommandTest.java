package com.example.sudoku.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import com.example.sudoku.commands.HintCommand;
import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuGenerator;


public class HintCommandTest {
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
    void hintCommand_whenEmptyCellsExist_thenFillsOneWithSolutionValueAndShowsHint() {
        // Given board with empty non-prefilled cells
        int beforeEmpties = board.getEmptyNonPrefilledCells().size();
        
        // When hint executed
        HintCommand cmd = new HintCommand();
        cmd.execute(board, solution, null);
        
        // Then one less empty, shows hint location/value
        int afterEmpties = board.getEmptyNonPrefilledCells().size();
        assertEquals(beforeEmpties - 1, afterEmpties);
        String output = outContent.toString();
        assertTrue(output.contains("Hint: cell"));
        assertTrue(output.contains("Hint: cell"));
    }

    @Test
    void hintCommand_whenNoEmptyNonPrefilled_thenShowsNoHintsAvailable() {
        // Given board with no empty non-prefilled cells (all filled or prefilled)
        for (int[] cell : board.getEmptyNonPrefilledCells()) {
            board.set(cell[0], cell[1], solution[cell[0]][cell[1]]);
        }
        
        // When hint executed
        HintCommand cmd = new HintCommand();
        cmd.execute(board, solution, null);
        
        // Then shows no hints message
        String output = outContent.toString();
        assertTrue(output.contains("No available hints"));
    }

    @Test
    void hintCommand_usesSolutionValueNotArbitrary() {
        // Given puzzle generated from known solution
        List<int[]> empties = board.getEmptyNonPrefilledCells();
        assertFalse(empties.isEmpty()); // ensure we have empties
        
        // When executed
        HintCommand cmd = new HintCommand();
        cmd.execute(board, solution, null);
        
        // Then some empty cell was filled with solution value
        boolean found = false;
        for (int[] cell : empties) {
            int r = cell[0], c = cell[1];
            if (board.get(r, c) == solution[r][c]) {
                found = true;
                break;
            }
        }
        assertTrue(found, "No solution value placed from empties");
    }
}
