#!/bin/bash

# 檢查參數
if [ "$#" -ne 1 ]; then
  echo "用法: $0 <備份文件路徑>"
  exit 1
fi

BACKUP_FILE=$1

# 檢查文件是否存在
if [ ! -f "${BACKUP_FILE}" ]; then
  echo "錯誤: 備份文件 ${BACKUP_FILE} 不存在"
  exit 1
fi

# 設置數據庫名稱
DB_NAME="calendar_system"

# 確認恢復操作
echo "警告: 這將覆蓋 ${DB_NAME} 數據庫的當前內容。"
read -p "是否繼續? (y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
  echo "操作已取消"
  exit 0
fi

# 執行恢復操作
echo "正在從 ${BACKUP_FILE} 恢復 ${DB_NAME} 數據庫..."
mysql -u root ${DB_NAME} < ${BACKUP_FILE}

# 檢查是否成功
if [ $? -eq 0 ]; then
  echo "數據庫恢復成功！"
else
  echo "數據庫恢復失敗！"
fi
