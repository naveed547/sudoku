package com.example.sudoku.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

import com.example.sudoku.Board;
import com.example.sudoku.utils.SudokuGenerator;

public class CommandImplTest {
    private Board board;
    private int[][] solution;
    private Random rand;

    @BeforeEach
    void setUp() {
        rand = new Random(42);
        board = new Board();
        SudokuGenerator gen = new SudokuGenerator(rand);
        solution = gen.generateFullSolution();
        gen.createPuzzle(board, solution, 30);
    }

    @Test
    void placeCommand_whenValidMove_thenBoardUpdatesAndReturnsSuccessMessage() {
        PlaceCommand cmd = new PlaceCommand("A1", "5");

        CommandResult result = cmd.execute(board, solution, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Placed 5 at A1"));

        assertEquals(5, board.get(0, 0));
    }

    @Test
    void placeCommand_whenPrefilled_thenRejects() {
        board.setPrefilled(0, 0, true);
        PlaceCommand cmd = new PlaceCommand("A1", "5");

        CommandResult result = cmd.execute(board, solution, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Cannot modify"));

        assertEquals(0, board.get(0, 0));
    }

    @Test
    void clearCommand_whenNonPrefilledFilledCell_thenClears() {
        board.set(0, 0, 7);
        board.setPrefilled(0, 0, false);

        ClearCommand cmd = new ClearCommand("A1");
        CommandResult result = cmd.execute(board, solution, null);

        assertTrue(result.success);
        assertEquals(0, board.get(0, 0));
    }

    @Test
    void clearCommand_whenPrefilled_thenRejects() {
        board.setPrefilled(0, 0, true);
        board.set(0, 0, 7);

        ClearCommand cmd = new ClearCommand("A1");
        CommandResult result = cmd.execute(board, solution, null);

        assertTrue(result.success);
        assertTrue(result.message.contains("Cannot clear"));
        assertEquals(7, board.get(0, 0));
    }

    @Test
    void hintCommand_whenEmptiesExist_thenFillsOne() {
        int beforeCount = board.getEmptyNonPrefilledCells().size();

        HintCommand cmd = new HintCommand();
        CommandResult result = cmd.execute(board, solution, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Hint: cell"));

        int afterCount = board.getEmptyNonPrefilledCells().size();
        assertEquals(beforeCount - 1, afterCount);
    }

    @Test
    void checkCommand_whenValid_thenReturnsReportFormat() {
        CheckCommand cmd = new CheckCommand();
        CommandResult result = cmd.execute(board, solution, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Problems")
                || result.message.contains("No rule violations")
                || result.message.contains("No duplicates"));
    }

    @Test
    void quitCommand_thenReturnsFalseToExitGame() {
        QuitCommand cmd = new QuitCommand();

        CommandResult result = cmd.execute(board, solution, null);

        assertFalse(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Quitting"));
    }

    @Test
    void unknownCommand_thenShowsHelpMessage() {
        UnknownCommand cmd = new UnknownCommand();

        CommandResult result = cmd.execute(board, solution, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Unknown"));
    }

    @Test
    void helpCommand_thenShowsCommandsList() {
        HelpCommand cmd = new HelpCommand();

        CommandResult result = cmd.execute(board, solution, null);

        assertTrue(result.success);
        assertNotNull(result.message);
        assertTrue(result.message.contains("Commands:"));
        assertTrue(result.message.contains("A3 5"));
        assertTrue(result.message.contains("hint"));
        assertTrue(result.message.contains("check"));
    }
}

