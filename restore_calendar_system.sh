#!/bin/bash

# Set backup directory
BACKUP_DIR="database_backups"

# Check parameters
if [ "$#" -ne 1 ]; then
  echo "No backup file path provided, will use the latest backup file..."
  
  # Check if backup directory exists
  if [ ! -d "${BACKUP_DIR}" ]; then
    echo "Error: Backup directory ${BACKUP_DIR} not found"
    echo "Creating backup directory..."
    
    mkdir -p "${BACKUP_DIR}" 2>/dev/null
    
    if [ $? -ne 0 ]; then
      echo "Failed to create backup directory"
      echo "Current directory: $(pwd)"
      exit 1
    else
      echo "Backup directory created successfully"
    fi
    
    echo "Please place backup files in the ${BACKUP_DIR} directory, then run this script again"
    exit 1
  fi
  
  # Find the latest SQL backup file
  LATEST_BACKUP=$(ls -t "${BACKUP_DIR}"/*.sql 2>/dev/null | head -n 1)
  
  if [ -z "${LATEST_BACKUP}" ]; then
    echo "Error: No .sql backup files found in ${BACKUP_DIR} directory"
    exit 1
  fi
  
  BACKUP_FILE="${LATEST_BACKUP}"
  echo "Found latest backup file: ${BACKUP_FILE}"
else
  BACKUP_FILE=$1
fi

# Check if file exists
if [ ! -f "${BACKUP_FILE}" ]; then
  echo "Error: Backup file ${BACKUP_FILE} does not exist"
  exit 1
fi

# Set database name
DB_NAME="calendar_system"
DB_USER="root"
DB_PASSWORD="123456"

# Confirm restore operation
echo "Warning: This will overwrite the current contents of the ${DB_NAME} database."
read -p "Continue? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
  echo "Operation cancelled"
  exit 0
fi

# Perform restore operation
echo "Restoring ${DB_NAME} database from ${BACKUP_FILE}..."
if [ -z "${DB_PASSWORD}" ]; then
  mysql -u ${DB_USER} ${DB_NAME} < "${BACKUP_FILE}"
else
  mysql -u ${DB_USER} -p${DB_PASSWORD} ${DB_NAME} < "${BACKUP_FILE}"
fi

# Check if successful
if [ $? -eq 0 ]; then
  echo "Database restore successful!"
else
  echo "Database restore failed!"
  echo "Possible reasons:"
  echo "- MySQL service is not running"
  echo "- Database ${DB_NAME} does not exist"
  echo "- Username or password is incorrect"
  echo "- Invalid backup file format"
fi
