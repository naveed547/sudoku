package com.example.sudoku.commands;

import java.util.Scanner;
import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuUtils;

public class ClearCommand implements Command {
    private final String cell;

    public ClearCommand(String cell) {
        this.cell = cell;
    }

    @Override
    public CommandResult execute(Board board, int[][] solution, Scanner sc) {
        int[] rc = SudokuUtils.parseCell(cell);
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
        board.clear(r, c);
        return CommandResult.continueGame("\nCleared " + cell.toUpperCase() + "\n");
    }
}

