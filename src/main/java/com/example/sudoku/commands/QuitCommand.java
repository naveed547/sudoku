package com.example.sudoku.commands;

import com.example.sudoku.Board;

public class QuitCommand implements Command {
    @Override
    public CommandResult execute(Board board) {
        return CommandResult.quit("\nQuitting. Bye! \n");
    }

    public static Command parse(String[] t) {
        return (t.length == 1 && "quit".equals(t[0].toLowerCase())) ? new QuitCommand() : null;
    }
}

