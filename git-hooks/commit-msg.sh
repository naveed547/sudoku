#!/bin/sh
# Commit-msg hook to enforce Conventional Commits format.

MSG_FILE=$1
FIRST_LINE=$(head -n 1 "$MSG_FILE")

# Regex based on your .gitmessage types: feat, fix, docs, style, refactor, test, chore
PATTERN="^(feat|fix|docs|style|refactor|test|chore)(\([a-z0-9-]+\))?: .+"

if ! echo "$FIRST_LINE" | grep -iqE "$PATTERN"; then
    echo "ERROR: Invalid commit message format."
    echo "Format must follow: <type>(<scope>): <subject>"
    echo "Valid types: feat, fix, docs, style, refactor, test, chore"
    echo "Example: feat(board): add ANSI screen clearing"
    exit 1
fi

# Subject length limit (50 characters as per .gitmessage)
if [ ${#FIRST_LINE} -gt 50 ]; then
    echo "ERROR: Commit subject line exceeds 50 characters."
    exit 1
fi

exit 0