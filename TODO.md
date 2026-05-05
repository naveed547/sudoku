# TODO: Command Pattern Refactor

## Plan Approved
- Command interface
- Concrete Commands (Quit, Hint, Check, Clear, Place, Unknown, Help)
- CommandFactory.parse()
- SudokuGame as invoker

## Steps:
- [ ] Create Command.java interface
- [ ] Create concrete Command classes
- [ ] Create CommandFactory.java
- [ ] Refactor SudokuGame.start() to use CommandFactory + loop
- [ ] Update tests if needed
- [ ] Test mvn clean package + run
