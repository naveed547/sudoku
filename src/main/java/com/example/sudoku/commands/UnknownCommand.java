package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.BoardRenderer;

public class UnknownCommand implements Command {
    @Override
    public CommandResult execute(Board board) {
        String msg = "\nUnknown command format.\n";
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        if (board != null) {
            sb.append(BoardRenderer.renderToString(board));
            sb.append(BoardRenderer.getPrompt(board));
        }
        return CommandResult.continueGame(sb.toString());
    }
}
