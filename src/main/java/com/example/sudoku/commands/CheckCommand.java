package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.BoardRenderer;
import com.example.sudoku.utils.ValidationResult;
import com.example.sudoku.utils.SudokuValidator;

public class CheckCommand implements Command {
    @Override
    public CommandResult execute(Board board) {
        // Validate the grid and report violations, but NEVER reject/correct the last move here.
        // Player inputs are allowed; `check` is where the player learns what is wrong.
        ValidationResult result = SudokuValidator.validateWholeBoard(board.toArrayCopy());

        StringBuilder sb = new StringBuilder();
        if (result.isValid()) {
            sb.append("No rule violations detected.");
        } else {
            sb.append("Problems found:");
            for (String p : result.getProblems()) {
                sb.append("\n - ").append(p);
            }
        }

        StringBuilder res = new StringBuilder();
        res.append("\n").append(sb).append("\n");
        res.append(BoardRenderer.renderToString(board));
        res.append(BoardRenderer.getPrompt(board));

        return CommandResult.continueGame(res.toString());
    }

    public static Command parse(String[] t) {
        return (t.length == 1 && "check".equals(t[0].toLowerCase())) ? new CheckCommand() : null;
    }
}