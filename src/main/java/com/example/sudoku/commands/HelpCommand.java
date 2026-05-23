package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.BoardRenderer;

public class HelpCommand implements Command {
    private static final String HELP_TEXT = 
        "\nCommands:\n" +
        "  A3 5     - Place 5 at A3\n" +
        "  A3 clear - Clear A3 (non-prefilled)\n" +
        "  hint     - Show random hint (Max " + Board.MAX_HINT_ALLOWED + ")\n" +
        "  check    - Validate board\n" +
        "  restart  - Restart board\n" +
        "  solve    - Solve entire puzzle\n" +
        "  help     - Show this\n" +
        "  quit     - Exit";

    @Override
    public CommandResult execute(Board board) {
        return CommandResult.continueGame(HELP_TEXT + BoardRenderer.getPrompt(board));
    }

    public static Command parse(String[] t) {
        return (t.length == 1 && "help".equals(t[0].toLowerCase())) ? new HelpCommand() : null;
    }
}
