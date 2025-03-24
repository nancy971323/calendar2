package com.company.calendar.controller;

import com.company.calendar.service.home.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 首頁服務
     */
    private final HomeService homeService;
    
    /**
     * 構造函數
     * 
     * @param homeService 首頁服務
     */
    @Autowired
    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    /**
     * 獲取系統基本信息
     * 
     * @return 系統信息
     */
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        return homeService.getSystemInfo();
    }
    
    /**
     * 系統健康檢查
     * 
     * @return 健康狀態
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        return homeService.checkHealth();
    }
}