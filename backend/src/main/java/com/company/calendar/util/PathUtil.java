package com.company.calendar.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 跨平台路徑處理工具
 * 處理Windows和Mac/Linux之間的路徑差異
 */
@Component
public class PathUtil {

    @Value("${app.file.separator:#{T(java.io.File).separator}}")
    private String fileSeparator;

    @Value("${app.backup.dir:./database_backups}")
    private String backupDir;

    /**
     * 獲取系統適配的文件分隔符
     * @return 當前系統的文件分隔符
     */
    public String getFileSeparator() {
        return fileSeparator;
    }

    /**
     * 獲取備份目錄路徑
     * @return 備份目錄的絕對路徑
     */
    public String getBackupDir() {
        // 確保目錄存在
        File dir = new File(backupDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getAbsolutePath();
    }

    /**
     * 將路徑標準化為當前系統的格式
     * @param path 輸入路徑
     * @return 標準化後的路徑
     */
    public String normalizePath(String path) {
        if (path == null) {
            return null;
        }
        
        // 使用Java NIO的Path類來處理路徑標準化
        Path normalizedPath = Paths.get(path).normalize();
        return normalizedPath.toString();
    }

    /**
     * 組合路徑
     * @param basePath 基礎路徑
     * @param relativePath 相對路徑
     * @return 組合後的路徑
     */
    public String combinePath(String basePath, String relativePath) {
        if (basePath == null || relativePath == null) {
            return null;
        }
        
        Path path = Paths.get(basePath, relativePath);
        return path.toString();
    }

    /**
     * 獲取備份文件的完整路徑
     * @param fileName 文件名
     * @return 完整的備份文件路徑
     */
    public String getBackupFilePath(String fileName) {
        return combinePath(getBackupDir(), fileName);
    }
} 