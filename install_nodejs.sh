#!/bin/bash

echo "[INFO] Checking for npm installation..."

# Check if npm is installed
if ! command -v npm &> /dev/null; then
    echo "[ERROR] npm is not installed or not in PATH"
    echo "[INFO] Installing Node.js and npm..."
    
    # Detect OS
    if [[ "$OSTYPE" == "linux-gnu"* ]]; then
        # Linux installation
        echo "[INFO] Linux detected, using apt or dnf for installation"
        
        # Try apt (Debian/Ubuntu)
        if command -v apt &> /dev/null; then
            echo "[INFO] Using apt package manager"
            curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
            sudo apt-get install -y nodejs
        # Try dnf (Fedora/RHEL)
        elif command -v dnf &> /dev/null; then
            echo "[INFO] Using dnf package manager"
            curl -fsSL https://rpm.nodesource.com/setup_18.x | sudo bash -
            sudo dnf install -y nodejs
        # Fallback to default package manager
        else
            echo "[WARNING] Could not identify package manager, trying with curl"
            curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.1/install.sh | bash
            export NVM_DIR="$HOME/.nvm"
            [ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
            nvm install 18
            nvm use 18
        fi
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS installation
        echo "[INFO] macOS detected, using Homebrew for installation"
        
        # Check if Homebrew is installed
        if ! command -v brew &> /dev/null; then
            echo "[INFO] Installing Homebrew first"
            /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
        fi
        
        brew install node@18
    else
        echo "[ERROR] Unsupported operating system: $OSTYPE"
        exit 1
    fi
    
    echo "[INFO] Node.js and npm installation completed"
else
    echo "[INFO] npm is already installed"
fi

# Check if installation was successful
if command -v npm &> /dev/null; then
    echo "[INFO] Node.js $(node -v) and npm $(npm -v) are installed"
else
    echo "[ERROR] Failed to install Node.js and npm"
    exit 1
fi 