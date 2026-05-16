package com.example.sudoku.commands;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandResultTest {

    @Test
    void continueGame_setsSuccessTrueAndMessage() {
        CommandResult r = CommandResult.continueGame("go");
        assertTrue(r.success);
        assertEquals("go", r.message);
    }

    @Test
    void quit_setsSuccessFalseAndMessage() {
        CommandResult r = CommandResult.quit("bye");
        assertFalse(r.success);
        assertEquals("bye", r.message);
    }

    @Test
    void constructor_setsFields() {
        CommandResult r = new CommandResult(true, "x");
        assertTrue(r.success);
        assertEquals("x", r.message);
    }
}
