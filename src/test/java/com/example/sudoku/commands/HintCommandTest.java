package com.example.sudoku.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuGenerator;

public class HintCommandTest {
    private Board board;
    private int[][] solution;
    private Random rand;

    @BeforeEach
    void setUp() {
        rand = new Random(42);

        board = new Board();
        SudokuGenerator gen = new SudokuGenerator(rand);
        solution = gen.generateFullSolution();
        gen.createPuzzle(board, solution, 30);
    }

    @Test
    void hintCommand_whenEmptyCellsExist_thenFillsOneWithSolutionValueAndReturnsHint() {
        int beforeEmpties = board.getEmptyNonPrefilledCells().size();

        HintCommand cmd = new HintCommand();
        CommandResult result = cmd.execute(board, solution, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Hint: cell"));

        int afterEmpties = board.getEmptyNonPrefilledCells().size();
        assertEquals(beforeEmpties - 1, afterEmpties);
    }

    @Test
    void hintCommand_whenNoEmptyNonPrefilled_thenShowsNoHintsAvailable() {
        List<int[]> empties = board.getEmptyNonPrefilledCells();
        for (int[] cell : empties) {
            board.set(cell[0], cell[1], solution[cell[0]][cell[1]]);
        }

        HintCommand cmd = new HintCommand();
        CommandResult result = cmd.execute(board, solution, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("No available hints"));
    }

    @Test
    void hintCommand_usesSolutionValueNotArbitrary() {
        List<int[]> beforeEmpties = board.getEmptyNonPrefilledCells();
        assertFalse(beforeEmpties.isEmpty());

        HintCommand cmd = new HintCommand();
        CommandResult result = cmd.execute(board, solution, null);

        assertTrue(result.success);
        assertNotNull(result.message);

        boolean found = false;
        for (int[] cell : beforeEmpties) {
            int r = cell[0], c = cell[1];
            if (board.get(r, c) == solution[r][c]) {
                found = true;
                break;
            }
        }
        assertTrue(found, "No solution value placed from empties");
    }
}

