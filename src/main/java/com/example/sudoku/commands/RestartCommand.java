package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.BoardRenderer;

public class RestartCommand implements Command {
    @Override
    public CommandResult execute(Board board) {
        board.restartCurrentPuzzle();
        String msg = String.format("\nGame restarted \n%s%s", 
                BoardRenderer.renderToString(board), 
                BoardRenderer.getPrompt(board));
        return CommandResult.continueGame(msg);
    }

    public static Command parse(String[] t) {
        return (t.length == 1 && "restart".equals(t[0].toLowerCase())) ? new RestartCommand() : null;
    }
}
