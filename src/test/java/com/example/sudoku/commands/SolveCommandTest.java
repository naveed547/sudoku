package com.example.sudoku.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuGenerator;

public class SolveCommandTest {
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
        board.setSolution(solution);
    }

    @Test
    void solveCommand_whenExecuted_fillsAllNonPrefilledCellsFromSolution() {
        int beforeEmpties = board.getEmptyNonPrefilledCells().size();
        assertTrue(beforeEmpties > 0, "Expected at least one non-prefilled empty cell for this test.");

        // keep a snapshot of prefilled values (they must remain unchanged)
        int[][] before = board.toArrayCopy();

        SolveCommand cmd = new SolveCommand();
        CommandResult result = cmd.execute(board);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Solved"));
        assertTrue(result.message.contains("Current grid:"));
        assertTrue(result.message.contains("Press ENTER for a new game"));

        int afterEmpties = board.getEmptyNonPrefilledCells().size();
        assertEquals(0, afterEmpties);

        // prefilled cells should not change
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (board.isPrefilled(r, c)) {
                    assertEquals(before[r][c], board.get(r, c));
                }
            }
        }

        // and every non-prefilled cell should match the stored solution
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (!board.isPrefilled(r, c)) {
                    assertEquals(solution[r][c], board.get(r, c));
                }
            }
        }
    }

    @Test
    void solveCommand_overwritesIncorrectUserMoves() {
        // Find a cell that isn't prefilled
        int r = -1, c = -1;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (!board.isPrefilled(i, j)) {
                    r = i; c = j; break;
                }
            }
            if (r != -1) break;
        }

        // Place an incorrect value (different from solution)
        int wrongVal = (solution[r][c] % 9) + 1; 
        board.placeValue(r, c, wrongVal);

        // Execute solve and verify correction
        new SolveCommand().execute(board);
        assertEquals(solution[r][c], board.get(r, c), "Solve should correct user mistakes");
    }
}
