package com.example.sudoku.commands;

import java.util.Scanner;
import com.example.sudoku.Board;


public class HelpCommand implements Command {
    @Override
    public boolean execute(Board board, int[][] solution, Scanner sc) {
        StringBuilder sb = new StringBuilder();
        sb.append("Commands:\n");
        sb.append("  A3 5     - Place 5 at A3\n");
        sb.append("  A3 clear - Clear A3 (non-prefilled)\n");
        sb.append("  hint     - Show random hint\n");
        sb.append("  check    - Validate board\n");
        sb.append("  help     - Show this\n");
        sb.append("  quit     - Exit\n\n");
        System.out.print(sb.toString());
        return true;
    }
}

