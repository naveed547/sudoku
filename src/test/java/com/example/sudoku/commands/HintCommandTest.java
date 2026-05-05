package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.SudokuGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

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
        assertTrue(output.matches(".*cell [A-I][1-9] = [1-9].*"));
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
        int r = 0, c = 0;
        int expectedValue = solution[r][c];
        
        // Assume A1 is empty (deterministic seed ensures)
        if (board.get(r,c) != 0) {
            // use first empty
            for (int i = 0; i < 9; i++) for (int j = 0; j < 9; j++) {
                if (board.get(i,j) == 0 && !board.isPrefilled(i,j)) {
                    r = i; c = j; expectedValue = solution[i][j];
                    break;
                }
            }
        }
        
        // When executed
        HintCommand cmd = new HintCommand();
        cmd.execute(board, solution, null);
        
        // Then first empty filled with correct solution value
        assertEquals(expectedValue, board.get(r,c));
    }
}
