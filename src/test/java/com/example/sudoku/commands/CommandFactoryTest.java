package com.example.sudoku.commands;

import com.example.sudoku.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {

    private final Board board = new Board();

    private void assertParsesTo(String input, Class<? extends Command> expected) {
        Command cmd = CommandFactory.parse(input, board);
        assertNotNull(cmd);
        assertInstanceOf(expected, cmd);
    }

    @Test
    void parse_basicCommands() {
        assertParsesTo("quit", QuitCommand.class);
        assertParsesTo("hint", HintCommand.class);
        assertParsesTo("check", CheckCommand.class);
        assertParsesTo("help", HelpCommand.class);
    }

    @Test
    void parse_clearCommand_edgeCases() {
        assertParsesTo("A1 clear", ClearCommand.class);
        assertParsesTo("a1  clear", ClearCommand.class);
        assertParsesTo("A1 Clear", ClearCommand.class);
        assertParsesTo("a1  Clear", ClearCommand.class);
        assertParsesTo("A1 CLEAR", ClearCommand.class);
        assertParsesTo("a1  CLEAR", ClearCommand.class);

        assertParsesTo("clear", UnknownCommand.class);
        assertParsesTo("clear A1", UnknownCommand.class);
        assertParsesTo("clear   a1", UnknownCommand.class);
        assertParsesTo("clear", UnknownCommand.class);
    }

    @Test
    void parse_placeCommand_edgeCases() {
        assertParsesTo("A1 5", PlaceCommand.class);
        assertParsesTo("a1 5", PlaceCommand.class);
        assertParsesTo("A1 x", UnknownCommand.class); // invalid number

        // CommandFactory only checks that the 2nd token is numeric.
        // It delegates cell-reference validation to PlaceCommand.
        assertParsesTo("A0 5", PlaceCommand.class);  // invalid row but still numeric => PlaceCommand
        assertParsesTo("J1 5", PlaceCommand.class);  // invalid row but still numeric => PlaceCommand
        assertParsesTo("AA 5", PlaceCommand.class);  // invalid cell format but still numeric => PlaceCommand
    }

    @Test
    void parse_caseInsensitivityForNoArgCommands() {
        assertParsesTo("QUIT", QuitCommand.class);
        assertParsesTo("HINT", HintCommand.class);
        assertParsesTo("CHECK", CheckCommand.class);
        assertParsesTo("HELP", HelpCommand.class);
    }

    @Test
    void parse_unknownCommand() {
        assertParsesTo("foobar", UnknownCommand.class);
        assertParsesTo("unknown_command", UnknownCommand.class);
        assertParsesTo("hint blah", UnknownCommand.class);
        assertParsesTo("Hint blah", UnknownCommand.class);
        assertParsesTo("check blah", UnknownCommand.class);
        assertParsesTo("Check blah", UnknownCommand.class);
        assertParsesTo("quit blah", UnknownCommand.class);
        assertParsesTo("QUIT blah", UnknownCommand.class);
        assertParsesTo("help blah", UnknownCommand.class);
        assertParsesTo("Help blah", UnknownCommand.class);
        assertParsesTo("quit blah", UnknownCommand.class);
        assertParsesTo("Quit blah", UnknownCommand.class);

        assertParsesTo("clear A3 B9", UnknownCommand.class);
        assertParsesTo("A3 4 extra", UnknownCommand.class);
    }

    @Test
    void parse_nullOrEmpty_returnsUnknownCommand() {
        assertInstanceOf(UnknownCommand.class, CommandFactory.parse(null, board));
        assertInstanceOf(UnknownCommand.class, CommandFactory.parse("", board));
        assertInstanceOf(UnknownCommand.class, CommandFactory.parse("   ", board));
    }

    @Test
    void performance_parse_manyInputs_noExceptions() {
        // Avoid strict timing thresholds to prevent flaky tests.
        for (int i = 0; i < 50_000; i++) {
            CommandFactory.parse((i % 2 == 0 ? "hint" : "A1 5"), board);
            CommandFactory.parse("clear A1", board);
            CommandFactory.parse("unknown_command", board);
        }
    }
}
