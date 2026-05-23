package com.example.sudoku.commands;

import com.example.sudoku.Board;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlaceCommandTest {

    @Test
    void execute_withInvalidCell_returnsErrorMessage() {
        Board board = new Board();
        PlaceCommand cmd = new PlaceCommand("Z9", "5");
        CommandResult result = cmd.execute(board);
        assertTrue(result.message.contains("Invalid cell reference"));
        assertTrue(result.message.contains("Here is your puzzle:"));
    }

    @Test
    void execute_withNonNumericValue_returnsErrorMessage() {
        Board board = new Board();
        PlaceCommand cmd = new PlaceCommand("A1", "X");
        CommandResult result = cmd.execute(board);
        assertTrue(result.message.contains("must be a number"));
        assertTrue(result.message.contains("Here is your puzzle:"));
    }

    @Test
    void execute_successfulPlacement_returnsSuccessMessage() {
        Board board = new Board();
        PlaceCommand cmd = new PlaceCommand("A1", "5");
        CommandResult result = cmd.execute(board);
        assertTrue(result.message.contains("Placed 5 at A1"));
        assertEquals(5, board.get(0, 0));
        assertTrue(result.message.contains("Current grid:"));
    }

    @Test
    void execute_onPrefilledCell_returnsErrorMessage() {
        Board board = new Board();
        board.set(0, 0, 5);
        board.setPrefilled(0, 0, true);
        PlaceCommand cmd = new PlaceCommand("A1", "6");
        CommandResult result = cmd.execute(board);
        assertTrue(result.message.contains("Cannot modify a prefilled cell"));
        assertEquals(5, board.get(0, 0));
        assertTrue(result.message.contains("Here is your puzzle:"));
    }

    @Test
    void execute_outOfRangeValue_returnsErrorMessage() {
        Board board = new Board();
        PlaceCommand cmd = new PlaceCommand("A1", "10");
        CommandResult result = cmd.execute(board);
        assertTrue(result.message.contains("must be between 1 and 9"));
        assertTrue(result.message.contains("Here is your puzzle:"));
    }

    @Test
    void execute_completingPuzzle_returnsCompletionMessage() {
        Board board = new Board();
        int[][] sol = {{5,3,4,6,7,8,9,1,2}, {6,7,2,1,9,5,3,4,8}, {1,9,8,3,4,2,5,6,7}, {8,5,9,7,6,1,4,2,3}, {4,2,6,8,5,3,7,9,1}, {7,1,3,9,2,4,8,5,6}, {9,6,1,5,3,7,2,8,4}, {2,8,7,4,1,9,6,3,5}, {3,4,5,2,8,6,1,7,9}};
        board.setSolution(sol);
        // Fill everything except A1
        for (int r=0; r<9; r++) for (int c=0; c<9; c++) if (r!=0 || c!=0) board.set(r, c, sol[r][c]);
        
        PlaceCommand cmd = new PlaceCommand("A1", "5");
        CommandResult result = cmd.execute(board);
        assertTrue(result.message.contains("successfully completed"));
        assertTrue(result.message.contains("Press ENTER for a new game"));
    }

    @Test
    void parse_acceptsValidFormat() {
        Command cmd = PlaceCommand.parse(new String[]{"B2", "7"});
        assertNotNull(cmd);
        assertInstanceOf(PlaceCommand.class, cmd);
    }
}