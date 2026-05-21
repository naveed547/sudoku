package com.example.sudoku.commands;

import java.util.List;

import com.example.sudoku.Board;

/**
 * Factory for creating Command instances from user input strings.
 * Each command registers its own static parser; factory iterates them in order.
 */
public class CommandFactory {

    private static final List<Command.Parser> PARSERS = List.of(
            QuitCommand::parse,
            HintCommand::parse,
            CheckCommand::parse,
            HelpCommand::parse,
            ClearCommand::parse,
            PlaceCommand::parse
    );

    public static Command parse(String line, Board board) {
        if (line == null || line.isBlank()) {
            return new UnknownCommand();
        }

        String[] parts = line.trim().split("\\s+");

        for (Command.Parser parser : PARSERS) {
            Command cmd = parser.parse(parts);
            if (cmd != null) {
                return cmd;
            }
        }

        return new UnknownCommand();
    }
}
