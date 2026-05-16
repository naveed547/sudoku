package com.example.sudoku;

import java.util.Scanner;
import com.example.sudoku.commands.*;
import com.example.sudoku.commands.CommandResult;

import com.example.sudoku.utils.SudokuValidator;

/**
 * Main entry point for the Sudoku CLI game.
 * Manages user input/output, integrates Board, Generator, and Utils.
 */
public class SudokuGame {
    /**
     * Program entry point - starts the interactive Sudoku game.
     */
    public static void main(String[] args) {
        new SudokuGame().start();
    }

    private void start() {
        System.out.println("Welcome to Sudoku! (Prefilled cells are fixed and cannot be changed)\n");
        Board board = new Board();
        int[][] solution = board.generateAndSetPuzzle();
        Scanner sc = new Scanner(System.in);
        while (true) {
            board.display();

            System.out.println("Enter command (eg: A3 4, clear C5, hint, check, quit): ");
            if (SudokuValidator.isCompleteAndValid(board.toArrayCopy())) {
                System.out.println("You have successfully completed the Sudoku puzzle!");
                System.out.println("Press ENTER to play again...");
                sc.nextLine();
                // new puzzle (reuse same Board/Generator)
                board = new Board();
                solution = board.generateAndSetPuzzle();
                continue;
            }

            String line = sc.nextLine().trim();
            if (line.isEmpty())
                continue;
            line = line.replace(",", " ").trim();
            Command cmd = CommandFactory.parse(line, board, solution, sc);
            CommandResult result = cmd.execute(board, solution, sc);
            if (result != null && result.message != null) {
                System.out.print(result.message);
            }
            if (result != null && !result.success) {
                break;
            }

        }
        sc.close();
    }
}
