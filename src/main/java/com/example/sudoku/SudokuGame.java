package com.example.sudoku;

import java.util.Random;
import java.util.Scanner;
import com.example.sudoku.commands.*;
import com.example.sudoku.utils.*;

/**
 * Main entry point for the Sudoku CLI game.
 * Manages user input/output, integrates Board, Generator, and Utils.
 */
public class SudokuGame {
    private static final Random rand = new Random();

    /**
     * Program entry point - starts the interactive Sudoku game.
     */
    public static void main(String[] args) {
        new SudokuGame().start();
    }

    private void start() {
        System.out.println("Welcome to Sudoku! (Prefilled cells are fixed and cannot be changed)\\n");
        Board board = new Board();
        SudokuGenerator gen = new SudokuGenerator(rand);
        int[][] solution = gen.generateFullSolution();
        gen.createPuzzle(board, solution, 30);
        Scanner sc = new Scanner(System.in);

        boolean first = true;
        while (true) {
            board.display(first);
            first = false;
            System.out.print("Enter command (eg: A3 4 , C5 clear , hint , check , quit): ");
            if (SudokuUtils.isCompleteAndValid(board.toArrayCopy())) {
                System.out.println("Congratulations — puzzle complete and valid. You win!");
                break;
            }
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            line = line.replace(",", " ").trim();
            Command cmd = CommandFactory.parse(line, board, solution, sc, rand);
            if (!cmd.execute(board, solution, sc)) {
                break;
            }
        }
        sc.close();
    }
}
