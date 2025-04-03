package com.company.calendar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密碼配置類
 * <p>
 * 提供密碼編碼器的配置，從SecurityConfig分離出來以避免循環依賴。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-04-03
 */
@Configuration
public class PasswordConfig {
    
    /**
     * 創建密碼編碼器Bean
     * 
     * @return 密碼編碼器，使用BCrypt算法
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 