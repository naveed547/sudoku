package com.example.sudoku.commands;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.example.sudoku.Board;

public class HintCommand implements Command {
    private static final Random RNG = new Random();

    @Override
    public CommandResult execute(Board board, int[][] solution, Scanner sc) {
        List<int[]> empties = board.getEmptyNonPrefilledCells();
        if (empties.isEmpty()) {
            return CommandResult.continueGame("\nNo available hints.\n");
        }
        int[] cell = empties.get(HintCommand.RNG.nextInt(empties.size()));

        int r = cell[0], c = cell[1];
        board.set(r, c, solution[r][c]);

        return CommandResult.continueGame(
                "\nHint: cell " + (char) ('A' + r) + (c + 1) + " = " + solution[r][c] + "\n"
        );
    }
}

