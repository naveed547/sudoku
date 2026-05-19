package com.example.sudoku.commands;

import java.util.List;
import java.util.Scanner;

import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuValidator;

public class CheckCommand implements Command {
    @Override
    public CommandResult execute(Board board, int[][] solution, Scanner sc) {
        // Validate the grid and report violations, but NEVER reject/correct the last move here.
        // Player inputs are allowed; `check` is where the player learns what is wrong.
        List<String> problems = SudokuValidator.validateWholeBoard(board.toArrayCopy());

        StringBuilder sb = new StringBuilder();
        if (problems.isEmpty()) {
            sb.append("No rule violations detected.");
        } else {
            sb.append("Problems found:");
            for (String p : problems) {
                sb.append("\n - ").append(p);
            }
        }

        return CommandResult.continueGame("\n" + sb + "\n");
    }
}