package com.example.sudoku.commands;

import com.example.sudoku.commands.Command;
import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuUtils;
import java.util.List;
import java.util.Scanner;

public class CheckCommand implements Command {
    @Override
    public boolean execute(Board board, int[][] solution, Scanner sc) {
        List<String> problems = SudokuUtils.validateWholeBoard(board.toArrayCopy());
        if (problems.isEmpty()) {
            System.out.println("\nNo duplicates detected.\n");
        } else {
            System.out.println("\nProblems found:");
            problems.forEach(p -> System.out.println(" - " + p));
            System.out.println();
        }
        return true;
    }
}
