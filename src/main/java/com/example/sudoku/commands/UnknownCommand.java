package com.example.sudoku.commands;

import java.util.Scanner;
import com.example.sudoku.Board;


public class UnknownCommand implements Command {
    @Override
    public boolean execute(Board board, int[][] solution, Scanner sc) {
        System.out.println("\nUnknown command format.\n");
        return true;
    }
}

