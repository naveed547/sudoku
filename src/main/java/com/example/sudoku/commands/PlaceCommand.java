package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.Command;
import com.example.sudoku.utils.SudokuUtils;
import java.util.Scanner;

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
            System.out.println("Invalid cell reference.");
            return true;
        }
        int r = rc[0], c = rc[1];
        if (board.isPrefilled(r, c)) {
            System.out.println("Cannot modify a prefilled cell.");
            return true;
        }
        int val;
        try {
            val = Integer.parseInt(valueToken);
        } catch (NumberFormatException e) {
            System.out.println("Second token must be a number 1-9.");
            return true;
        }
        if (val < 1 || val > 9) {
            System.out.println("Number must be between 1 and 9.");
            return true;
        }
        if (!SudokuUtils.isValidMove(board.toArrayCopy(), r, c, val)) {
            System.out.println("Invalid move: would create a duplicate in row, column, or 3x3 box.");
            return true;
        }
        board.set(r, c, val);
        System.out.println("Placed " + val + " at " + cell.toUpperCase());
        return true;
    }
}
