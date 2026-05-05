package com.example.sudoku.commands;

import com.example.sudoku.commands.Command;
import com.example.sudoku.Board;
import java.util.Scanner;

public class QuitCommand implements Command {
    @Override
    public boolean execute(Board board, int[][] solution, Scanner sc) {
        System.out.println("Quitting. Bye!");
        return false;
    }
}
