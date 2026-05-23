package com.example.sudoku.commands;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuGenerator;

public class RestartAndSolveIntegrationTest {
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

        // Ensure board knows the solution for commands like solve/hint.
        board.setSolution(solution);
    }

    @Test
    void solve_thenRestart_clearsSolvedCells_andResetsHintCap() {
        // Before solve: there should be at least one empty non-prefilled cell.
        int beforeEmpties = board.getEmptyNonPrefilledCells().size();
        assertTrue(beforeEmpties > 0, "Expected at least one empty non-prefilled cell before solve.");

        int beforeHintsLeft = board.getHintCountLeft();

        // Solve the puzzle first.
        SolveCommand solve = new SolveCommand();
        CommandResult solveResult = solve.execute(board);

        assertTrue(solveResult.success);
        assertNotNull(solveResult.message);
        assertTrue(solveResult.message.contains("Solved"));

        // After solve: no empty non-prefilled cells should remain.
        int afterSolveEmpties = board.getEmptyNonPrefilledCells().size();
        assertEquals(0, afterSolveEmpties);

        // Restart should reset the board to a new puzzle state (clears user entries).
        RestartCommand restart = new RestartCommand();
        CommandResult restartResult = restart.execute(board);

        assertTrue(restartResult.success);
        assertNotNull(restartResult.message);
        assertTrue(restartResult.message.contains("Game restarted"));

        int afterRestartEmpties = board.getEmptyNonPrefilledCells().size();
        assertTrue(afterRestartEmpties > 0, "Expected empty cells again after restart.");

        // Hint cap should reset to MAX_HINT_ALLOWED.
        assertEquals(Board.MAX_HINT_ALLOWED, board.getHintCountLeft());

        // Sanity: hint count should have changed after restart vs before solve (usually).
        // Not strictly required, so only assert it's within the expected range.
        assertTrue(beforeHintsLeft >= 0 && beforeHintsLeft <= Board.MAX_HINT_ALLOWED);
    }

    @Test
    void solve_allAtOnce_fillsFromSolution_evenIfBoardHintCountLeftChanged() {
        // Consume a couple of hints to change hint counter (solve should not depend on hint counter).
        board.consumeHint();
        board.consumeHint();
        assertEquals(Board.MAX_HINT_ALLOWED - 2, board.getHintCountLeft());

        SolveCommand solve = new SolveCommand();
        CommandResult result = solve.execute(board);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Solved"));

        // Every non-prefilled cell should match stored solution.
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (!board.isPrefilled(r, c)) {
                    assertEquals(solution[r][c], board.get(r, c));
                }
            }
        }
    }
}
