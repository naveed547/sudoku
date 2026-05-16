package com.example.sudoku.commands;

/**
 * Result of executing a CLI command.
 *
 * - success: controls whether the main game loop continues.
 * - message: user-facing text to be printed by the game shell (no printing inside commands).
 */
public class CommandResult {
    public final boolean success;
    public final String message;

    public CommandResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static CommandResult continueGame(String message) {
        return new CommandResult(true, message);
    }

    public static CommandResult quit(String message) {
        return new CommandResult(false, message);
    }
}

