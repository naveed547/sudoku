package com.example.sudoku.commands;

import java.util.Map;
import java.util.Scanner;
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
            "help", HelpCommand::new);

    private static final Map<String, Function<String, Command>> ARG_COMMANDS = Map.of(
            "clear", ClearCommand::new);

    public static Command parse(String line, Board board, int[][] solution, Scanner sc) {

        if (line == null || line.isBlank()) {
            return new UnknownCommand();
        }

        String[] parts = line.trim().split("\\s+");
        String cmd = parts[0].toLowerCase();

        // Commands without arguments
        Supplier<Command> noArg = COMMANDS.get(cmd);
        if (noArg != null) {
            return parts.length == 1
                    ? noArg.get()
                    : new UnknownCommand();
        }

        // Commands like: A5 clear
        if (parts.length == 2 && "clear".equalsIgnoreCase(parts[1])) {
            Function<String, Command> oneArg = ARG_COMMANDS.get(parts[1].toLowerCase());

            if (oneArg != null) {
                return oneArg.apply(parts[0]);
            }
        }

        // Commands like: A5 7
        if (parts.length == 2 && parts[1].matches("\\d+")) {
            return new PlaceCommand(parts[0], parts[1]);
        }

        return new UnknownCommand();
    }
}
