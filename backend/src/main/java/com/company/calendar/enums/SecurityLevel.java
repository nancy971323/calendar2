package com.company.calendar.enums;

/**
 * 安全等級枚舉
 * <p>
 * 定義系統中的安全等級，從最高權限（LEVEL_1）到最低權限（LEVEL_4）。
 * 此枚舉用於設定員工權限以及事件的可見性限制。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
public enum SecurityLevel {
    /**
     * 最高安全等級（等級1）
     * 通常分配給系統管理員或公司高層主管
     */
    LEVEL_1(1, "最高權限"),
    
    /**
     * 第二安全等級（等級2）
     * 通常分配給部門主管或中層管理人員
     */
    LEVEL_2(2, "高級權限"),
    
    /**
     * 第三安全等級（等級3）
     * 通常分配給團隊領導或資深員工
     */
    LEVEL_3(3, "中級權限"),
    
    /**
     * 最低安全等級（等級4）
     * 通常分配給一般員工
     */
    LEVEL_4(4, "基本權限");
    
    private final int level;
    private final String description;
    
    /**
     * 構造函數
     * 
     * @param level 權限等級數值
     * @param description 權限等級描述
     */
    SecurityLevel(int level, String description) {
        this.level = level;
        this.description = description;
    }
    
    /**
     * 獲取權限等級數值
     * 
     * @return 權限等級數值
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * 獲取權限等級描述
     * 
     * @return 權限等級描述
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * 檢查當前等級是否高於或等於指定等級
     * 
     * @param other 要比較的權限等級
     * @return 若當前等級高於或等於指定等級，則返回true
     */
    public boolean hasAccessTo(SecurityLevel other) {
        // 數值越小，權限越高
        return this.level <= other.level;
    }
    
    /**
     * 根據等級數值獲取對應的權限等級枚舉
     * 
     * @param level 權限等級數值
     * @return 對應的權限等級枚舉，若未找到則返回null
     */
    public static SecurityLevel fromLevel(int level) {
        for (SecurityLevel securityLevel : SecurityLevel.values()) {
            if (securityLevel.level == level) {
                return securityLevel;
            }
        }
        return null;
    }
}