#!/data/data/com.termux/files/usr/bin/bash

clear

echo "===================================="
echo " EchoWave GitHub Auto Upload Script "
echo "===================================="
echo ""

pkg install git -y > /dev/null 2>&1

read -p "Enter project folder path: " PROJECT_PATH
read -p "Enter GitHub username: " GITHUB_USER
read -p "Enter repository name: " REPO_NAME

cd "$PROJECT_PATH" || {
  echo "Invalid project path"
  exit 1
}

echo ""
echo "Fixing safe.directory..."
git config --global --add safe.directory "$PROJECT_PATH"

echo ""
echo "Initializing git..."
git init

git branch -M main

echo ""
echo "Adding files..."
git add .

echo ""
echo "Creating commit..."
git commit -m "Initial commit"

echo ""
echo "Adding remote repository..."
git remote remove origin 2>/dev/null

git remote add origin "https://github.com/$GITHUB_USER/$REPO_NAME.git"

echo ""
echo "===================================="
echo " GitHub login required next"
echo " Username = your GitHub username"
echo " Password = your GitHub token"
echo "===================================="
echo ""

git push -u origin main

echo ""
echo "===================================="
echo " Upload complete"
echo " Open GitHub -> Actions"
echo " APK build will start automatically"
echo "===================================="
