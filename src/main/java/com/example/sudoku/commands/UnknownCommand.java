package com.example.sudoku.commands;

import com.example.sudoku.commands.Command;
import com.example.sudoku.Board;
import java.util.Scanner;

public class UnknownCommand implements Command {
    @Override
    public boolean execute(Board board, int[][] solution, Scanner sc) {
        System.out.println("\nUnknown command format.\n");
        return true;
    }
}
