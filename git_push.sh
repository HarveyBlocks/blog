#!/bin/bash

# Git auto commit and push script
# Usage: ./git_auto.sh

# generate json file for file tree
python ./json_file_tree_generator.py
echo "[INFO]: successfully update json file tree"
# Check if current directory is a git repository
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    echo "Error: Not a git repository"
    exit 1
fi

# Get current date and time
COMMIT_DATE=$(date +"%Y-%m-%d %H:%M:%S")

# Git operations
git add . > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Error: git add failed"
    exit 1
fi

git commit -m "Auto commit: $COMMIT_DATE" > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Nothing to commit"
    exit 0
fi

git push -u origin main > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Error: git push failed"
    exit 1
fi

echo "[INFO]: Success: Commit and push completed"
read var
exit 0