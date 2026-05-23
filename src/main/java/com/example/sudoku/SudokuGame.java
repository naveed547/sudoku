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
        System.out.print(BoardRenderer.getWelcomeMessage());
        System.out.print(BoardRenderer.renderToString(board) + BoardRenderer.getPrompt(board));

        try (Scanner sc = new Scanner(System.in)) {
            while (sc.hasNextLine()) {
                try {
                    String line = sc.nextLine().trim();
                    line = line.replace(",", " ").trim();
                    Command cmd = CommandFactory.parse(line, board, gameService);
                    CommandResult result = cmd.execute(board);

                    if (result != null) {
                        System.out.print(result.message);
                        if (!result.success) break;
                    }
                } catch (Exception e) {
                    System.out.println("\nAn unexpected error occurred: " + e.getMessage());
                    System.out.print(BoardRenderer.getPrompt(board));
                }
            }
        }
    }
}