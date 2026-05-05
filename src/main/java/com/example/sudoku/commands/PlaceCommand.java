package com.example.sudoku.commands;

import java.util.Scanner;

import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuUtils;


public class PlaceCommand implements Command {
    private final String cell;
    private final String valueToken;

    public PlaceCommand(String cell, String valueToken) {
        this.cell = cell;
        this.valueToken = valueToken;
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
            System.out.println("\nCannot modify a prefilled cell.\n");
            return true;
        }

        int val;
        try {
            val = Integer.parseInt(valueToken);
        } catch (NumberFormatException e) {
            System.out.println("\nSecond token must be a number 1-9.\n");
            return true;
        }

        if (val < 1 || val > 9) {
            System.out.println("\nNumber must be between 1 and 9.\n");
            return true;
        }

        // Accept the move even if it creates a rule violation.
        // The player will learn/see violations when they run `check`.
        board.set(r, c, val);
        System.out.println("\nPlaced " + val + " at " + cell.toUpperCase() + "\n");
        return true;
    }
}

