package com.example.sudoku.commands;

import com.example.sudoku.commands.Command;
import com.example.sudoku.Board;
import java.util.Scanner;

public class HelpCommand implements Command {
    @Override
    public boolean execute(Board board, int[][] solution, Scanner sc) {
        System.out.println("\nCommands:");
        System.out.println("  A3 5     - Place 5 at A3");
        System.out.println("  A3 clear - Clear A3 (non-prefilled)");
        System.out.println("  hint     - Show random hint");
        System.out.println("  check    - Validate board");
        System.out.println("  help     - Show this");
        System.out.println("  quit     - Exit\n");
        return true;
    }
}
