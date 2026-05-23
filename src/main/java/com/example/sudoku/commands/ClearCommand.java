package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.BoardRenderer;
import com.example.sudoku.utils.SudokuValidator;

public class ClearCommand implements Command {
    private final String cell;

    public ClearCommand(String cell) {
        this.cell = cell;
    }

    @Override
    public CommandResult execute(Board board) {
        int[] rc = SudokuValidator.parseCell(cell);
        if (rc == null || rc.length < 2) {
            return CommandResult.continueGame("\nInvalid cell reference.\n" + BoardRenderer.renderToString(board) + BoardRenderer.getPrompt(board));
        }

        int r = rc[0], c = rc[1];
        if (r < 0 || r >= Board.SIZE || c < 0 || c >= Board.SIZE) {
            return CommandResult.continueGame("\nInvalid cell reference.\n" + BoardRenderer.renderToString(board) + BoardRenderer.getPrompt(board));
        }

        if (board.isPrefilled(r, c)) {
            return CommandResult.continueGame("\nCannot clear a prefilled cell.\n" + BoardRenderer.renderToString(board) + BoardRenderer.getPrompt(board));
        }
        if (board.get(r, c) == 0) {
            return CommandResult.continueGame("\nCell already empty.\n" + BoardRenderer.renderToString(board) + BoardRenderer.getPrompt(board));
        }

        if (!board.clearCell(r, c)) {
            return CommandResult.continueGame("\nCell could not be cleared.\n" + BoardRenderer.renderToString(board) + BoardRenderer.getPrompt(board));
        }

        String msg = String.format("\nCleared %s\n%s%s", cell.toUpperCase(), BoardRenderer.renderToString(board), BoardRenderer.getPrompt(board));
        return CommandResult.continueGame(msg);
    }

    public static Command parse(String[] t) {
        return (t.length == 2 && "clear".equalsIgnoreCase(t[1])) ? new ClearCommand(t[0]) : null;
    }
}
