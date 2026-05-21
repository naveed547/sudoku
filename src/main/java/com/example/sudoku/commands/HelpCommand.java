package com.example.sudoku.commands;

import com.example.sudoku.Board;

public class HelpCommand implements Command {
    @Override
    public CommandResult execute(Board board) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nCommands:\n");
        sb.append("  A3 5     - Place 5 at A3\n");
        sb.append("  A3 clear - Clear A3 (non-prefilled)\n");
        sb.append("  hint     - Show random hint\n");
        sb.append("  check    - Validate board\n");
        sb.append("  help     - Show this\n");
        sb.append("  quit     - Exit");
        return CommandResult.continueGame(sb.toString());
    }

    public static Command parse(String[] t) {
        return (t.length == 1 && "help".equals(t[0].toLowerCase())) ? new HelpCommand() : null;
    }
}

