package com.example.sudoku.utils;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ValidationResultTest {
    @Test
    void isValid_returnsTrueWhenNoErrors() {
        ValidationResult res = new ValidationResult(List.of());
        assertTrue(res.isValid());
        assertTrue(res.getProblems().isEmpty());
    }

    @Test
    void getProblems_formatsRowErrorCorrectly() {
        ValidationResult.ValidationError err = new ValidationResult.ValidationError(
            ValidationResult.ErrorType.ROW, 5, 0, -1);
        ValidationResult res = new ValidationResult(List.of(err));

        assertFalse(res.isValid());
        List<String> problems = res.getProblems();
        assertEquals(1, problems.size());
        assertEquals("Duplicate 5 in row A", problems.get(0));
    }

    @Test
    void getProblems_formatsColumnErrorCorrectly() {
        ValidationResult.ValidationError err = new ValidationResult.ValidationError(
            ValidationResult.ErrorType.COLUMN, 9, -1, 4);
        ValidationResult res = new ValidationResult(List.of(err));

        assertEquals("Duplicate 9 in column 5", res.getProblems().get(0));
    }

    @Test
    void getProblems_formatsBoxErrorCorrectly() {
        ValidationResult.ValidationError err = new ValidationResult.ValidationError(
            ValidationResult.ErrorType.BOX, 2, 3, 6);
        ValidationResult res = new ValidationResult(List.of(err));

        assertEquals("Duplicate 2 in 3x3 box starting at D7", res.getProblems().get(0));
    }
}