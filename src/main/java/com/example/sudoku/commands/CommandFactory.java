package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.commands.Command;
import java.util.Random;
import java.util.Scanner;

/**
 * Factory for creating Command instances from user input strings.
 */
public class CommandFactory {
    public static Command parse(String line, Board board, int[][] solution, Scanner sc, Random rand) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length == 0) {
            return new UnknownCommand();
        }
        
        String cmd = parts[0].toLowerCase();
        
        switch (cmd) {
            case "quit":
                return new QuitCommand();
            case "hint":
                return new HintCommand();
            case "check":
                return new CheckCommand();
            case "help":
                return new HelpCommand();
            case "clear":
                if (parts.length >= 2) {
                    return new ClearCommand(parts[1]);
                }
                return new UnknownCommand();
            default:
                if (parts.length >= 2) {
                    try {
                        Integer.parseInt(parts[1]);
                        return new PlaceCommand(parts[0], parts[1]);
                    } catch (NumberFormatException e) {
                        // not place
                    }
                }
                return new UnknownCommand();
        }
    }
}
