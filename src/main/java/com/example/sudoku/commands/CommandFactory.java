package com.example.sudoku.commands;

import java.util.Map;
import java.util.function.Function;

import com.example.sudoku.GameService;
import com.example.sudoku.Board;

/**
 * Factory for creating Command instances from user input strings.
 * Each command registers its own static parser; factory iterates them in order.
 */
public class CommandFactory {

    private static final Map<String, Function<String[], Command>> SINGLE_WORD_COMMANDS = Map.of(
            "quit", QuitCommand::parse,
            "hint", HintCommand::parse,
            "check", CheckCommand::parse,
            "help", HelpCommand::parse,
            "solve", SolveCommand::parse,
            "restart", RestartCommand::parse
    );


    public static Command parse(String line, Board board, GameService gameService) {
        if (line == null || line.isBlank()) {
            return new RefreshCommand(gameService);
        }

        String[] parts = line.trim().split("\\s+");
        String firstToken = parts[0].toLowerCase();

        // 1. Process single-word commands (solve, restart, quit, etc.)
        if (parts.length == 1 && SINGLE_WORD_COMMANDS.containsKey(firstToken)) {
            return SINGLE_WORD_COMMANDS.get(firstToken).apply(parts);
        }

        // 2. If solved, intercept remaining inputs (coordinates/invalid text) to guide the user.
        // We do this AFTER single-word commands so 'help' or 'restart' still work.
        if (board != null && board.isSolved()) {
            return b -> CommandResult.continueGame(
                "\nPuzzle solved! Press ENTER for a new game or type 'quit' to exit.\n");
        }

        Command clearCmd = ClearCommand.parse(parts);
        if (clearCmd != null) return clearCmd;

        Command placeCmd = PlaceCommand.parse(parts);
        if (placeCmd != null) return placeCmd;

        return new UnknownCommand();
    }
}
