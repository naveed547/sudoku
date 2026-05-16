package com.example.sudoku.commands;

import java.util.Scanner;
import com.example.sudoku.Board;


/**
 * Interface for all Sudoku commands.
 */
public interface Command {
    CommandResult execute(Board board, int[][] solution, Scanner sc);
}

