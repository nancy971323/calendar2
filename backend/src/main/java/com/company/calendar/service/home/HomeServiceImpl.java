package com.company.calendar.service.home;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 首頁服務實現類
 * <p>
 * 實現HomeService介面，提供系統基本信息和健康檢查功能的具體實現。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Service
public class HomeServiceImpl implements HomeService {

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "企業行事曆系統");
        info.put("version", "1.0.0");
        info.put("description", "基於Spring Boot和Vue的企業行事曆系統，支持多級權限控制");
        
        Map<String, String> contact = new HashMap<>();
        contact.put("name", "系統管理員");
        contact.put("email", "admin@example.com");
        info.put("contact", contact);
        
        return info;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> checkHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        
        return health;
    }
} 