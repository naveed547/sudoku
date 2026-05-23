package com.example.sudoku;

/**
 * Utility for rendering the board and common game messages.
 */
public class BoardRenderer {

    /**
     * ANSI escape codes to clear the screen and move the cursor to the top-left.
     * Works on most modern terminals (Windows 10+, macOS, Linux).
     */
    public static String clearScreen() {
        return "\033[H\033[2J";
    }

    public static String getWelcomeMessage() {
        return "Welcome to Sudoku! (Prefilled cells are fixed and cannot be changed)\n";
    }

    public static String getCompletionMessage() {
        return "You have successfully completed the Sudoku puzzle!\nPress ENTER for a new game or type 'quit' to exit.\n";
    }

    public static String getPrompt(Board board) {
        int hints = (board != null) ? board.getHintCountLeft() : Board.MAX_HINT_ALLOWED;
        return "\nEnter command (eg: A3 4, C5 clear, hint(" + hints + "), check, restart, solve, quit, help): ";
    }

    public static String renderToString(Board board) {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append(board.isPuzzleStarted() ? "Current grid:" : "Here is your puzzle:").append('\n');
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
        return sb.toString();
    }
}