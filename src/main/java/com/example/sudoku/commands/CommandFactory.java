package com.example.sudoku.commands;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.example.sudoku.Board;

/**
 * Factory for creating Command instances from user input strings.
 */
public class CommandFactory {

    private static final Map<String, Supplier<Command>> COMMANDS = Map.of(
            "quit", QuitCommand::new,
            "hint", HintCommand::new,
            "check", CheckCommand::new,
            "help", HelpCommand::new
    );

    private static final Map<String, Function<String, Command>> ARG_COMMANDS = Map.of(
            "clear", ClearCommand::new
    );

    public static Command parse(String line, Board board, int[][] solution, java.util.Scanner sc) {
        if (line == null || line.trim().isEmpty()) {
            return new UnknownCommand();
        }

        String[] parts = line.trim().split("\\s+");
        if (parts.length == 0) {
            return new UnknownCommand();
        }

        String cmd = parts[0].toLowerCase();

        Supplier<Command> noArg = COMMANDS.get(cmd);
        if (noArg != null) {
            return noArg.get();
        }

        Function<String, Command> oneArg = ARG_COMMANDS.get(cmd);
        if (oneArg != null) {
            if (parts.length >= 2 && parts[1] != null && !parts[1].isBlank()) {
                return oneArg.apply(parts[1]);
            }
            return new UnknownCommand();
        }

        // Default: place command expects: <cell> <number>
        if (parts.length >= 2 && parts[1] != null && parts[1].matches("\\d+")) {
            return new PlaceCommand(parts[0], parts[1]);
        }

        return new UnknownCommand();
    }
}

