# TODO

- [ ] Introduce `CommandResult` DTO (success + message)
- [ ] Refactor `Command.execute(...)` to return `CommandResult`
- [x] Refactor all command implementations to remove `System.out.println` and return messages via `CommandResult`

- [ ] Update `SudokuGame` to print command messages based on `CommandResult` and use `success` to control loop
- [x] Update unit tests to assert on `CommandResult.message` / `success` instead of captured stdout

- [ ] Run `run-tests.ps1` / `mvn test` and fix any failing assertions/compilation issues

