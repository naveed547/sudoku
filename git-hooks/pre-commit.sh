#!/bin/sh
# Pre-commit hook to run Maven tests.

# Only run tests if there are staged changes in the src/ directory.
# This covers both production code and the test files themselves.
if ! git diff --cached --name-only | grep -q "^src/"; then
    echo "No changes in src/ detected. Skipping Maven tests."
    exit 0
fi

echo "Verifying project compilation..."
mvn compile
if [ $? -ne 0 ]; then
    echo "Compilation failed! Commit aborted. Please fix compilation errors before committing."
    exit 1
fi

echo "Running Maven tests..."
mvn test

if [ $? -ne 0 ]; then
    echo "Tests failed! Commit aborted. Please fix the tests before committing."
    exit 1
fi

exit 0