#!/bin/bash

# 設置變數
DB_NAME="calendar_system"
BACKUP_DIR="/Users/lidiannan/Documents/GitHub/calendar2/database_backups"
DATE=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/calendar_system_backup_${DATE}.sql"

# 確保備份目錄存在
mkdir -p ${BACKUP_DIR}

# 執行備份
echo "正在備份 ${DB_NAME} 數據庫到 ${BACKUP_FILE}..."
mysqldump -u root --add-drop-table --databases ${DB_NAME} --routines --triggers --events > ${BACKUP_FILE}

# 檢查備份是否成功
if [ $? -eq 0 ]; then
  echo "備份成功！檔案位置: ${BACKUP_FILE}"
  echo "檔案大小: $(du -h ${BACKUP_FILE} | cut -f1)"
else
  echo "備份失敗！"
fi
