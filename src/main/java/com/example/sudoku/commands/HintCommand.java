package com.example.sudoku.commands;

import java.util.List;
import java.util.Random;

import com.example.sudoku.Board;

public class HintCommand implements Command {
    private static final Random RNG = new Random();

    @Override
    public CommandResult execute(Board board) {
        List<int[]> empties = board.getEmptyNonPrefilledCells();
        if (empties.isEmpty()) {
            return CommandResult.continueGame("\nNo available hints.\n");
        }
        int[] cell = empties.get(HintCommand.RNG.nextInt(empties.size()));

        int r = cell[0], c = cell[1];
        int[][] sol = board.getSolution();
        int val = sol[r][c];

        boolean ok = board.applyHint(r, c, val);
        if (!ok) {
            // Should not happen because we only pick empty non-prefilled cells.
            return CommandResult.continueGame("\nNo available hints.\n");
        }

        return CommandResult.continueGame(
                "\nHint: cell " + (char) ('A' + r) + (c + 1) + " = " + val + "\n"
        );
    }

    public static Command parse(String[] t) {
        return (t.length == 1 && "hint".equals(t[0].toLowerCase())) ? new HintCommand() : null;
    }
}

