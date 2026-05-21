package com.example.sudoku;

import java.util.Scanner;

import com.example.sudoku.commands.Command;
import com.example.sudoku.commands.CommandFactory;
import com.example.sudoku.commands.CommandResult;
import com.example.sudoku.utils.SudokuGenerator;

/**
 * Main entry point. Owns the game loop: input → command → render → solved-check.
 */
public class SudokuGame {

    public static void main(String[] args) {
        new SudokuGame().run();
    }

    public void run() {
        SudokuGenerator generator = new SudokuGenerator();
        GameService gameService = new GameService(generator);
        Board board = gameService.startNewGame();
        board.printWelcome();

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                if (board.isSolved()) {
                    board.printCompletionSuccess();
                    sc.nextLine();
                    board = gameService.startNewGame();
                    board.printWelcome();
                    continue;
                }

                board.render(board.isPuzzleStarted());

                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                line = line.replace(",", " ").trim();
                Command cmd = CommandFactory.parse(line, board);
                CommandResult result = cmd.execute(board);

                if (result != null && result.message != null) {
                    System.out.print(result.message);
                }
                if (result != null && !result.success) {
                    break;
                }
                // First user action marks the puzzle as "in progress"
                if (!board.isPuzzleStarted()) {
                    board.setPuzzleStarted(true);
                }
            }
        }
    }
}