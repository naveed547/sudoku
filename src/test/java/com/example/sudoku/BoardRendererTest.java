package com.example.sudoku;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class BoardRendererTest {

    @Test
    void printWelcome_printsWelcomeText() {
        BoardRenderer renderer = new BoardRenderer();

        PrintStream originalOut = System.out;
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capture));

        try {
            renderer.printWelcome();
        } finally {
            System.setOut(originalOut);
        }

        String out = capture.toString();
        assertTrue(
                out.contains("Welcome to Sudoku! (Prefilled cells are fixed and cannot be changed)"),
                "printWelcome() should print the welcome message"
        );
    }

    @Test
    void printCompletionSuccess_printsCompletionPrompt() {
        BoardRenderer renderer = new BoardRenderer();

        PrintStream originalOut = System.out;
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capture));

        try {
            renderer.printCompletionSuccess();
        } finally {
            System.setOut(originalOut);
        }

        String out = capture.toString();
        assertTrue(
                out.contains("You have successfully completed the Sudoku puzzle!"),
                "printCompletionSuccess() should print completion message"
        );
        assertTrue(
                out.contains("Press ENTER to play again..."),
                "printCompletionSuccess() should prompt to press ENTER"
        );
    }

    @Test
    void render_whenPuzzleStartedTrue_printsCurrentGridHeader_andPrompt_andIncludesBoardContent() {
        BoardRenderer renderer = new BoardRenderer();
        Board board = new Board();
        board.set(0, 0, 5); // row A, col 1

        PrintStream originalOut = System.out;
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capture));

        try {
            renderer.render(board, true);
        } finally {
            System.setOut(originalOut);
        }

        String out = capture.toString();

        assertTrue(out.contains("Current grid:"), "render(..., true) should show Current grid header");
        assertTrue(out.contains("1 2 3 | 4 5 6 | 7 8 9"), "render should include column header");
        assertTrue(
                out.contains("Enter command (eg: A3 4, clear C5, hint, check, quit, help): "),
                "render should include command prompt"
        );

        // At least ensure board content shows the set value and empty cells as underscores
        assertTrue(out.contains("5"), "render should include the filled value");
        assertTrue(out.contains("_"), "render should include underscore for empty cells");
    }

    @Test
    void render_whenPuzzleStartedFalse_printsHereIsYourPuzzleHeader_andPrompt_andIncludesBoardContent() {
        BoardRenderer renderer = new BoardRenderer();
        Board board = new Board();
        board.set(8, 8, 9); // row I, col 9

        PrintStream originalOut = System.out;
        ByteArrayOutputStream capture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capture));

        try {
            renderer.render(board, false);
        } finally {
            System.setOut(originalOut);
        }

        String out = capture.toString();

        assertTrue(out.contains("Here is your puzzle:"), "render(..., false) should show Here is your puzzle header");
        assertTrue(out.contains("1 2 3 | 4 5 6 | 7 8 9"), "render should include column header");
        assertTrue(
                out.contains("Enter command (eg: A3 4, clear C5, hint, check, quit, help): "),
                "render should include command prompt"
        );

        assertTrue(out.contains("9"), "render should include the filled value");
        assertTrue(out.contains("_"), "render should include underscore for empty cells");
    }
}
