package com.example.sudoku.commands;

import com.example.sudoku.Board;
import com.example.sudoku.GameService;
import com.example.sudoku.utils.SudokuGenerator;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RefreshCommandTest {

    @Test
    void execute_onUnsolvedBoard_simplyRenders() {
        Board board = new Board();
        GameService service = new GameService(new SudokuGenerator());
        RefreshCommand cmd = new RefreshCommand(service);

        CommandResult result = cmd.execute(board);
        assertTrue(result.message.contains("Here is your puzzle:"));
        assertFalse(result.message.contains("Welcome to Sudoku!")); // Didn't restart
    }

    @Test
    void execute_onSolvedBoard_triggersNewGame() {
        Board board = new Board();
        GameService service = new GameService(new SudokuGenerator(new Random(42)));
        int[][] sol = service.startNewGame().getSolution();
        board.setSolution(sol);
        board.copyFromSolution(sol); // Force solve

        RefreshCommand cmd = new RefreshCommand(service);
        CommandResult result = cmd.execute(board);

        assertTrue(result.message.contains("Welcome to Sudoku!")); // Triggered restart
    }

    @Test
    void execute_onSolvedBoard_includesClearScreenSequence() {
        Board board = new Board();
        GameService service = new GameService(new SudokuGenerator(new Random(42)));
        int[][] sol = service.startNewGame().getSolution();
        board.setSolution(sol);
        board.copyFromSolution(sol); // Force solve

        RefreshCommand cmd = new RefreshCommand(service);
        CommandResult result = cmd.execute(board);

        assertTrue(result.message.contains("\033[H\033[2J"), "Message should contain ANSI clear screen sequence");
    }
}