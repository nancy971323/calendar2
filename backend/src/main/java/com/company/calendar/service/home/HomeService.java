package com.company.calendar.service.home;

import java.util.Map;

/**
 * 首頁服務介面
 * <p>
 * 定義處理系統基本信息和健康檢查的服務介面。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
public interface HomeService {
    
    /**
     * 獲取系統基本信息
     * 
     * @return 系統信息的Map
     */
    Map<String, Object> getSystemInfo();
    
    /**
     * 系統健康檢查
     * 
     * @return 健康狀態的Map
     */
    Map<String, Object> checkHealth();
} 