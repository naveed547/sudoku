package com.example.sudoku;

public class BoardRenderer {

    /**
     * Render the board to a string using the legacy display formatting.
     * @param board the board to render
     * @param puzzleStarted whether to show "Current grid:" instead of "Here is your puzzle:"
     * @return the rendered board as a string
     */
    public String render(Board board, boolean puzzleStarted) {
        StringBuilder sb = new StringBuilder();

        sb.append(puzzleStarted ? "Current grid:" : "Here is your puzzle:").append('\n');
        String colsHeader = "   1 2 3 | 4 5 6 | 7 8 9 ";
        sb.append(colsHeader).append('\n');
        sb.append("   ------+-------+------- ").append('\n');

        for (int r = 0; r < Board.SIZE; r++) {
            char rowLabel = (char) ('A' + r);
            sb.append(rowLabel).append("  ");

            for (int c = 0; c < Board.SIZE; c++) {
                int val = board.get(r, c);
                String cell = (val == 0) ? "_" : String.valueOf(val);
                sb.append(cell).append(" ");
                if (c % 3 == 2 && c < 8) sb.append("| ");
            }

            sb.append('\n');
            if (r % 3 == 2 && r < 8) {
                sb.append("   ------+-------+------- ").append('\n');
            }
        }

        return sb.toString() + "\n";
    }
}
