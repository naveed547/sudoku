package com.example.sudoku;

import com.example.sudoku.utils.SudokuGenerator;

/**
 * GameService contains the puzzle generation flow, so Board can focus on state only.
 */
public class GameService {

    private final SudokuGenerator generator;

    public GameService(SudokuGenerator generator) {
        this.generator = generator;
    }

    /**
     * Resets the provided board and generates a new puzzle.
     * @return full solution for validation/hints
     */
    public int[][] newPuzzle(Board board) {
        board.resetState();

        int[][] solution = generator.generateFullSolution();
        generator.createPuzzle(board, solution, Board.PREFILLED_COUNT);

        return solution;
    }
}
