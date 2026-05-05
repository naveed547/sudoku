package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.SudokuGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

public class CommandImplTest {
    private Board board;
    private int[][] solution;
    private Random rand;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        rand = new Random(42);
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        board = new Board();
        SudokuGenerator gen = new SudokuGenerator(rand);
        solution = gen.generateFullSolution();
        gen.createPuzzle(board, solution, 30);
    }

    @Test
    void placeCommand_whenValidMove_thenBoardUpdatesAndMessageShown() {
        // Given empty valid cell
        String cell = "A1";
        String value = "5";
        PlaceCommand cmd = new PlaceCommand(cell, value);
        
        // When executed
        boolean continueGame = cmd.execute(board, solution, null);
        
        // Then board updated, continue, proper message
        assertTrue(continueGame);
        assertEquals(5, board.get(0, 0));
        assertTrue(outContent.toString().contains("Placed 5 at A1"));
    }

    @Test
    void placeCommand_whenPrefilled_thenRejects() {
        // Given prefilled cell
        board.setPrefilled(0,0, true);
        PlaceCommand cmd = new PlaceCommand("A1", "5");
        
        // When executed
        cmd.execute(board, solution, null);
        
        // Then no change, rejection message
        assertEquals(0, board.get(0,0));
        assertTrue(outContent.toString().contains("Cannot modify"));
    }

    @Test
    void clearCommand_whenNonPrefilledFilledCell_thenClears() {
        // Given filled non-prefilled cell
        board.set(0,0, 7);
        board.setPrefilled(0,0, false);
        ClearCommand cmd = new ClearCommand("A1");
        
        // When executed
        cmd.execute(board, solution, null);
        
        // Then cleared
        assertEquals(0, board.get(0,0));
    }

    @Test
    void clearCommand_whenPrefilled_thenRejects() {
        // Given prefilled filled cell
        board.setPrefilled(0,0, true);
        board.set(0,0, 7);
        ClearCommand cmd = new ClearCommand("A1");
        
        // When executed
        cmd.execute(board, solution, null);
        
        // Then not cleared
        assertEquals(7, board.get(0,0));
    }

    @Test
    void hintCommand_whenEmptiesExist_thenFillsOne() {
        // Given board with empties
        int beforeCount = board.getEmptyNonPrefilledCells().size();
        HintCommand cmd = new HintCommand();
        
        // When executed
        cmd.execute(board, solution, null);
        
        // Then one less empty
        int afterCount = board.getEmptyNonPrefilledCells().size();
        assertEquals(beforeCount - 1, afterCount);
    }

    @Test
    void checkCommand_whenValid_thenNoProblems() {
        // Given valid puzzle state
        CheckCommand cmd = new CheckCommand();
        
        // When executed
        cmd.execute(board, solution, null);
        
        // Then no problems message
        assertTrue(outContent.toString().contains("No duplicates"));
    }

    @Test
    void quitCommand_thenReturnsFalse() {
        // Given
        QuitCommand cmd = new QuitCommand();
        
        // When executed
        boolean continueGame = cmd.execute(board, solution, null);
        
        // Then exits game
        assertFalse(continueGame);
    }

    @Test
    void unknownCommand_thenShowsHelpMessage() {
        // Given
        UnknownCommand cmd = new UnknownCommand();
        
        // When executed
        cmd.execute(board, solution, null);
        
        // Then unknown message
        assertTrue(outContent.toString().contains("Unknown"));
    }

    @Test
    void helpCommand_thenShowsCommandsList() {
        // Given
        HelpCommand cmd = new HelpCommand();
        
        // When executed
        cmd.execute(board, solution, null);
        
        // Then help text shown
        String output = outContent.toString();
        assertTrue(output.contains("Commands:"));
        assertTrue(output.contains("A1 5") || output.contains("place"));
        assertTrue(output.contains("hint") || output.contains("check"));
    }
}
