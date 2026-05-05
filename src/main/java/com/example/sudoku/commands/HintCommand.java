package com.example.sudoku.commands;

import com.example.sudoku.commands.Command;
import com.example.sudoku.Board;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class HintCommand implements Command {
    // Random passed in - no static field needed

    @Override
    public boolean execute(Board board, int[][] solution, Scanner sc) {
        List<int[]> empties = board.getEmptyNonPrefilledCells();
        if (empties.isEmpty()) {
            System.out.println("\nNo available hints.");
            return true;
        }
        int[] cell = empties.get(rand.nextInt(empties.size())); // rand from factory
        int r = cell[0], c = cell[1];
        board.set(r, c, solution[r][c]);
        System.out.printf("\nHint: cell %c%d = %d%n", (char) ('A' + r), (c + 1), solution[r][c]);
        return true;
    }
}
