package com.example.sudoku.commands;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import com.example.sudoku.Board;


public class HintCommand implements Command {
    // Random passed in - no static field needed

    @Override
    public boolean execute(Board board, int[][] solution, Scanner sc) {
        List<int[]> empties = board.getEmptyNonPrefilledCells();
        if (empties.isEmpty()) {
            System.out.println("\nNo available hints.\n");
            return true;
        }
        int[] cell = empties.get(new Random().nextInt(empties.size()));


        int r = cell[0], c = cell[1];
        board.set(r, c, solution[r][c]);

        System.out.println(
                "\nHint: cell " + (char) ('A' + r) + (c + 1) + " = " + solution[r][c] + "\n"
        );
        return true;
    }
}

