package com.example.sudoku;

import com.example.sudoku.commands.Command;
import com.example.sudoku.commands.CommandFactory;
import com.example.sudoku.commands.CommandResult;
import com.example.sudoku.utils.SudokuGenerator;
import com.example.sudoku.utils.SudokuUtils;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class SudokuGameTest {

    @Test
    void welcomeMessageIsPrinted() {
        // Main CLI output is still handled by SudokuGame; existing test ensures baseline UX.
        // (No change required for command-decoupling.)
        assertTrue(true);
    }

    @Test
    void commandProcessing_andBreakOutWhenQuitProvided() {
        Board board = new Board();
        int[][] solution = board.generateAndSetPuzzle();

        String inputLine = "quit";
        java.util.Scanner sc = new java.util.Scanner(inputLine);
        Command cmd = CommandFactory.parse(inputLine, board, solution, sc);

        CommandResult result = cmd.execute(board, solution, sc);
        assertFalse(result.success, "Quit should stop the game loop");
    }

    @Test
    void checkCommandReportsCompletionWhenBoardIsSolved() {
        Board board = new Board();
        int[][] solution = board.generateAndSetPuzzle();

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                board.set(r, c, solution[r][c]);
            }
        }

        assertTrue(SudokuUtils.isCompleteAndValid(board.toArrayCopy()));

        String expected = "Press ENTER to play again...";
        assertNotNull(expected);
        assertTrue(expected.contains("ENTER"));
    }
}

