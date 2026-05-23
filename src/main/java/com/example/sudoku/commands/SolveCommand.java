package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.BoardRenderer;

public class SolveCommand implements Command {

    @Override
    public CommandResult execute(Board board) {
        int[][] sol = board.getSolution();
        if (sol == null) {
            String err = String.format("\nNo solution available.\n%s%s", BoardRenderer.renderToString(board), BoardRenderer.getPrompt(board));
            return CommandResult.continueGame(err);
        }

        // Fill all non-prefilled cells using the stored solution.
        board.copyFromSolution(sol);
        board.setPuzzleStarted(true);

        String success = String.format("\nSolved the puzzle.\n%s%s", 
                BoardRenderer.renderToString(board), 
                BoardRenderer.getCompletionMessage());
        return CommandResult.continueGame(success);
    }

    public static Command parse(String[] t) {
        return (t.length == 1 && "solve".equals(t[0].toLowerCase())) ? new SolveCommand() : null;
    }
}
