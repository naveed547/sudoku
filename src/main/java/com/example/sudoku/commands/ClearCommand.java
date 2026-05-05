package com.example.sudoku.commands;

import com.example.sudoku.commands.Command;
import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuUtils;
import java.util.Scanner;

public class ClearCommand implements Command {
    private final String cell;

    public ClearCommand(String cell) {
        this.cell = cell;
    }

    @Override
    public boolean execute(Board board, int[][] solution, Scanner sc) {
        int[] rc = SudokuUtils.parseCell(cell);
        if (rc == null) {
            System.out.println("\nInvalid cell reference.\n");
            return true;
        }
        int r = rc[0], c = rc[1];
        if (board.isPrefilled(r, c)) {
            System.out.println("\nCannot clear a prefilled cell.\n");
            return true;
        }
        if (board.get(r, c) == 0) {
            System.out.println("\nCell already empty.\n");
            return true;
        }
        board.clear(r, c);
        System.out.printf("\nCleared %s%n\n", cell.toUpperCase());
        return true;
    }
}
