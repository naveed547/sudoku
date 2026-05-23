package com.example.sudoku.utils;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    public enum ErrorType { ROW, COLUMN, BOX }

    public static class ValidationError {
        public final ErrorType type;
        public final int value;
        public final int row;
        public final int col;

        public ValidationError(ErrorType type, int value, int row, int col) {
            this.type = type;
            this.value = value;
            this.row = row;
            this.col = col;
        }
    }

    private final List<ValidationError> errors;

    public ValidationResult(List<ValidationError> errors) {
        this.errors = List.copyOf(errors);
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getProblems() {
        List<String> problems = new ArrayList<>();
        for (ValidationError error : errors) {
            problems.add(formatError(error));
        }
        return problems;
    }

    private String formatError(ValidationError error) {
        switch (error.type) {
            case ROW:
                return "Duplicate " + error.value + " in row " + (char) ('A' + error.row);
            case COLUMN:
                return "Duplicate " + error.value + " in column " + (error.col + 1);
            case BOX:
                return "Duplicate " + error.value + " in 3x3 box starting at " + (char) ('A' + error.row) + (error.col + 1);
            default:
                return "Unknown error";
        }
    }
}