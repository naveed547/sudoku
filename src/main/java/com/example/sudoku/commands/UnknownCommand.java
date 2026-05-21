package com.example.sudoku.commands;

import com.example.sudoku.Board;

public class UnknownCommand implements Command {
    @Override
    public CommandResult execute(Board board) {
        return CommandResult.continueGame("\nUnknown command format.\n");
    }
}

