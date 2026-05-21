package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuValidator;

public class ClearCommand implements Command {
    private final String cell;

    public ClearCommand(String cell) {
        this.cell = cell;
    }

    @Override
    public CommandResult execute(Board board) {
        int[] rc = SudokuValidator.parseCell(cell);
        if (rc == null) {
            return CommandResult.continueGame("\nInvalid cell reference.\n");
        }
        int r = rc[0], c = rc[1];
        if (board.isPrefilled(r, c)) {
            return CommandResult.continueGame("\nCannot clear a prefilled cell.\n");
        }
        if (board.get(r, c) == 0) {
            return CommandResult.continueGame("\nCell already empty.\n");
        }

        boolean ok = board.clearCell(r, c);
        if (!ok) {
            return CommandResult.continueGame("\nCell could not be cleared.\n");
        }

        return CommandResult.continueGame("\nCleared " + cell.toUpperCase() + "\n");
    }

    public static Command parse(String[] t) {
        return (t.length == 2 && "clear".equalsIgnoreCase(t[1])) ? new ClearCommand(t[0]) : null;
    }
}

