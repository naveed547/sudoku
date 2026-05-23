package com.example.sudoku.commands;

import com.example.sudoku.GameService;
import com.example.sudoku.Board;
import com.example.sudoku.BoardRenderer;

public class RefreshCommand implements Command {
    private final GameService gameService;

    public RefreshCommand(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public CommandResult execute(Board board) {
        // If the game is already solved, hitting ENTER (Refresh) acts as an acknowledgment
        // and starts a brand new game using the same board container.
        StringBuilder sb = new StringBuilder();
        if (board.isSolved()) {
            gameService.prepareNewPuzzle(board);
            // Clear console and home cursor to provide a clean state for the new game
            sb.append("\033[H\033[2J");
            sb.append(BoardRenderer.getWelcomeMessage());
        }
        sb.append(BoardRenderer.renderToString(board));
        sb.append(BoardRenderer.getPrompt(board));

        return CommandResult.continueGame(sb.toString());
    }
}