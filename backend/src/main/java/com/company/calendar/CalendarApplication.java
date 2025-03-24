package com.company.calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 行事曆系統主應用程式類
 * <p>
 * 此類是Spring Boot應用程式的入口點，負責初始化並啟動整個行事曆系統。
 * 系統提供基於權限的事件管理功能，不同權限等級的用戶可以查看對應的事件。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@SpringBootApplication
public class CalendarApplication {

    /**
     * 應用程式入口點
     * 
     * @param args 命令行參數
     */
    public static void main(String[] args) {
        SpringApplication.run(CalendarApplication.class, args);
    }
}