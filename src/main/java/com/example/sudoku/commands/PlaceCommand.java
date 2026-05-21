package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuValidator;

public class PlaceCommand implements Command {
    private final String cell;
    private final String valueToken;

    public PlaceCommand(String cell, String valueToken) {
        this.cell = cell;
        this.valueToken = valueToken;
    }

    @Override
    public CommandResult execute(Board board) {
        int[] rc = SudokuValidator.parseCell(cell);
        if (rc == null) {
            return CommandResult.continueGame("\nInvalid cell reference.\n");
        }
        int r = rc[0], c = rc[1];
        if (board.isPrefilled(r, c)) {
            return CommandResult.continueGame("\nCannot modify a prefilled cell.\n");
        }

        int val;
        try {
            val = Integer.parseInt(valueToken);
        } catch (NumberFormatException e) {
            return CommandResult.continueGame("\nSecond token must be a number 1-9.\n");
        }

        if (val < 1 || val > 9) {
            return CommandResult.continueGame("\nNumber must be between 1 and 9.\n");
        }

        // Accept the move even if it creates a rule violation.
        // The player will learn/see violations when they run `check`.
        boolean ok = board.placeValue(r, c, val);
        if (!ok) {
            return CommandResult.continueGame("\nCannot place value on that cell.\n");
        }

        return CommandResult.continueGame("\nPlaced " + val + " at " + cell.toUpperCase() + "\n");
    }

    public static Command parse(String[] t) {
        return (t.length == 2 && t[1].matches("\\d+")) ? new PlaceCommand(t[0], t[1]) : null;
    }
}

