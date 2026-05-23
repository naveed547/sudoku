package com.example.sudoku;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardRendererTest {

    @Test
    void getWelcomeMessage_returnsWelcomeText() {
        String out = BoardRenderer.getWelcomeMessage();
        assertTrue(
                out.contains("Welcome to Sudoku! (Prefilled cells are fixed and cannot be changed)"),
                "getWelcomeMessage() should contain the welcome text"
        );
    }

    @Test
    void getCompletionMessage_returnsCompletionPrompt() {
        String out = BoardRenderer.getCompletionMessage();
        assertTrue(
                out.contains("You have successfully completed the Sudoku puzzle!"),
                "getCompletionMessage() should contain completion message"
        );
        assertTrue(
                out.contains("Press ENTER for a new game"),
                "getCompletionMessage() should prompt for a new game"
        );
    }

    @Test
    void renderToString_whenPuzzleStartedTrue_showsCurrentGridHeader() {
        Board board = new Board();
        board.set(0, 0, 5); // row A, col 1
        board.setPuzzleStarted(true);

        String out = BoardRenderer.renderToString(board);

        assertTrue(out.contains("Current grid:"), "renderToString() should show Current grid header when started");
        assertTrue(out.contains("1 2 3 | 4 5 6 | 7 8 9"), "render should include column header");
        assertTrue(out.contains("5"), "render should include the filled value");
    }

    @Test
    void renderToString_whenPuzzleStartedFalse_showsHereIsYourPuzzleHeader() {
        Board board = new Board();
        board.set(8, 8, 9); // row I, col 9
        board.setPuzzleStarted(false);

        String out = BoardRenderer.renderToString(board);

        assertTrue(out.contains("Here is your puzzle:"), "renderToString() should show puzzle header when not started");
        assertTrue(out.contains("9"), "render should include the filled value");
    }
}