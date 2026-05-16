## TODO - SRP refactor + tests

- [ ] Create `BoardRenderer` and move all display formatting out of `Board`
- [ ] Create `GameService` (or `SudokuGameService`) and move puzzle generation flow out of `Board`
- [ ] Refactor `Board` to be state-only (remove `generateAndSetPuzzle()` and `display()` and any puzzle-flow state)
- [ ] Update `SudokuGame` to use `GameService` + `BoardRenderer`
- [ ] Update/adjust `BoardTest`:
  - [ ] Keep state tests (`set/get/clear`, `prefilled` marking, empties)
  - [ ] Move generation tests to a new service test (e.g., `GameServiceTest`)
- [ ] Add new tests:
  - [ ] Service test verifying puzzle generation produces a valid complete solution and correct prefilled count
  - [ ] Renderer smoke test verifying rendering output contains expected row/labels and cell values
- [ ] Run `mvn test` and ensure all tests pass
