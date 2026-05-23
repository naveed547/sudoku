package com.example.sudoku.commands;

import java.util.List;
import java.util.Random;

import com.example.sudoku.Board;
import com.example.sudoku.BoardRenderer;

public class HintCommand implements Command {
    private static final Random RNG = new Random();

    @Override
    public CommandResult execute(Board board) {
        List<int[]> empties = board.getEmptyNonPrefilledCells();
        if (empties.isEmpty()) {
            return CommandResult.continueGame("\nNo available hints.\n" + BoardRenderer.renderToString(board) + BoardRenderer.getPrompt(board));
        }

        if (!board.consumeHint()) {
            return CommandResult.continueGame("\nMax hints cap reached.\n" + BoardRenderer.renderToString(board) + BoardRenderer.getPrompt(board));
        }

        int[] cell = empties.get(RNG.nextInt(empties.size()));
        int r = cell[0], c = cell[1];
        int val = board.getSolution()[r][c];

        if (!board.applyHint(r, c, val)) {
            return CommandResult.continueGame("\nNo available hints.\n" + BoardRenderer.renderToString(board) + BoardRenderer.getPrompt(board));
        }

        String msg = String.format("\nHint (%d remaining): cell %c%d = %d\n%s%s",
                board.getHintCountLeft(),
                (char) ('A' + r),
                c + 1,
                val,
                BoardRenderer.renderToString(board),
                BoardRenderer.getPrompt(board));
        return CommandResult.continueGame(msg);
    }

    public static Command parse(String[] t) {
        return (t.length == 1 && "hint".equals(t[0].toLowerCase())) ? new HintCommand() : null;
    }
}
