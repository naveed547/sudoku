package com.example.sudoku.commands;

import com.example.sudoku.Board;
import java.util.Scanner;

/**
 * Interface for all Sudoku commands.
 */
public interface Command {
    boolean execute(Board board, int[][] solution, Scanner sc);
}
