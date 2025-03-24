package com.company.calendar.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 首頁控制器
 * <p>
 * 處理首頁相關的HTTP請求，提供基本的系統信息。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/api")
public class HomeController {

    /**
     * 獲取系統基本信息
     * 
     * @return 系統信息
     */
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
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
     * 系統健康檢查
     * 
     * @return 健康狀態
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        
        return health;
    }
}