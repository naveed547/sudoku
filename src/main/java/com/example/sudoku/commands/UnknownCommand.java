package com.example.sudoku.commands;

import java.util.Scanner;

import com.example.sudoku.Board;

public class UnknownCommand implements Command {
    @Override
    public CommandResult execute(Board board, int[][] solution, Scanner sc) {
        return CommandResult.continueGame("\nUnknown command format.\n");
    }
}

