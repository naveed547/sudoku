package com.example.sudoku;

import com.example.sudoku.utils.SudokuGenerator;

/**
 * Puzzle generation service.
 * Creates puzzles — does NOT own the game loop or board state.
 */
public class GameService {

    private final SudokuGenerator generator;

    public GameService(SudokuGenerator generator) {
        this.generator = generator;
    }

    /**
     * Create a fresh board populated with a new puzzle.
     * @return the newly created board
     */
    public Board startNewGame() {
        Board board = new Board();
        board.resetState();

        int[][] solution = generator.generateFullSolution();
        generator.createPuzzle(board, solution, Board.PREFILLED_COUNT);
        board.setSolution(solution);
        return board;
    }
}