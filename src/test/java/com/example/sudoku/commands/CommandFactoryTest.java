package com.example.sudoku.commands;

import com.example.sudoku.Board;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {

    private final Board board = new Board();
    private final int[][] solution = new int[9][9];
    private final Scanner sc = new Scanner(System.in);

    private void assertParsesTo(String input, Class<? extends Command> expected) {
        Command cmd = CommandFactory.parse(input, board, solution, sc);
        assertNotNull(cmd);
        assertInstanceOf(expected, cmd);
    }

    @Test
    void parse_basicCommands() {
        assertParsesTo("quit", QuitCommand.class);
        assertParsesTo("hint", HintCommand.class);
        assertParsesTo("check", ValidateBoardCommand.class);
        assertParsesTo("help", HelpCommand.class);
    }

    @Test
    void parse_clearCommand_edgeCases() {
        assertParsesTo("clear A1", ClearCommand.class);
        assertParsesTo("clear   a1", ClearCommand.class); // spacing + case
        assertParsesTo("clear", UnknownCommand.class); // missing arg
    }

    @Test
    void parse_placeCommand_edgeCases() {
        assertParsesTo("A1 5", SetCellCommand.class);
        assertParsesTo("a1 5", SetCellCommand.class);
        assertParsesTo("A1 x", UnknownCommand.class); // invalid number

        // CommandFactory only checks that the 2nd token is numeric.
        // It delegates cell-reference validation to SetCellCommand.
        assertParsesTo("A0 5", SetCellCommand.class);  // invalid row but still numeric => SetCellCommand
        assertParsesTo("J1 5", SetCellCommand.class);  // invalid row but still numeric => SetCellCommand
        assertParsesTo("AA 5", SetCellCommand.class);  // invalid cell format but still numeric => SetCellCommand
    }

    @Test
    void parse_caseInsensitivityForNoArgCommands() {
        assertParsesTo("QUIT", QuitCommand.class);
        assertParsesTo("HINT", HintCommand.class);
        assertParsesTo("CHECK", ValidateBoardCommand.class);
        assertParsesTo("HELP", HelpCommand.class);
    }

    @Test
    void parse_unknownCommand() {
        assertParsesTo("foobar", UnknownCommand.class);
        assertParsesTo("unknown_command", UnknownCommand.class);
    }

    @Test
    void parse_nullOrEmpty_returnsUnknownCommand() {
        assertInstanceOf(UnknownCommand.class, CommandFactory.parse(null, board, solution, sc));
        assertInstanceOf(UnknownCommand.class, CommandFactory.parse("", board, solution, sc));
        assertInstanceOf(UnknownCommand.class, CommandFactory.parse("   ", board, solution, sc));
    }

    @Test
    void performance_parse_manyInputs_noExceptions() {
        // Avoid strict timing thresholds to prevent flaky tests.
        for (int i = 0; i < 50_000; i++) {
            CommandFactory.parse((i % 2 == 0 ? "hint" : "A1 5"), board, solution, sc);
            CommandFactory.parse("clear A1", board, solution, sc);
            CommandFactory.parse("unknown_command", board, solution, sc);
        }
    }
}
