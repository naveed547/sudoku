package com.example.sudoku.commands;

import com.example.sudoku.Board;


/**
 * Interface for all Sudoku commands.
 */
public interface Command {
    CommandResult execute(Board board);

    @FunctionalInterface
    interface Parser {
        Command parse(String[] tokens);
    }
}

