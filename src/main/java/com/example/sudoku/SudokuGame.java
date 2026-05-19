package com.example.sudoku;

import java.util.Scanner;

import com.example.sudoku.commands.Command;
import com.example.sudoku.commands.CommandFactory;
import com.example.sudoku.commands.CommandResult;
import com.example.sudoku.utils.SudokuValidator;
import com.example.sudoku.utils.SudokuGenerator;

/**
 * Main entry point for the Sudoku CLI game.
 * Manages user input/output and delegates board rendering + puzzle generation to services.
 */
public class SudokuGame {

    public static void main(String[] args) {
        new SudokuGame().start();
    }

    private void start() {
        
        Board board = new Board();
        BoardRenderer renderer = new BoardRenderer();
        GameService gameService = new GameService(new SudokuGenerator(new java.util.Random()));
        renderer.printWelcome();

        int[][] solution = gameService.newPuzzle(board);

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                renderer.render(board, board.isPuzzleStarted());

                if (SudokuValidator.isCompleteAndValid(board.toArrayCopy())) {
                    renderer.printCompletionSuccess();
                    sc.nextLine();

                    board = new Board();
                    gameService = new GameService(new SudokuGenerator(new java.util.Random()));
                    solution = gameService.newPuzzle(board);
                    continue;
                }

                String line = sc.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
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
        }
    }
}

