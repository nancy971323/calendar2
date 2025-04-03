package com.company.calendar.controller;

import com.company.calendar.model.Employee;
import com.company.calendar.service.auth.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 認證控制器
 * <p>
 * 處理用戶認證相關的HTTP請求，包括登入、登出和獲取當前用戶信息。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 認證服務
     */
    private final AuthService authService;
    
    /**
     * 構造函數
     * 
     * @param authService 認證服務
     */
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    /**
     * 處理登入請求
     * 
     * @param loginRequest 包含用戶名和密碼的Map
     * @return 登入結果，包含用戶信息和JWT令牌
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("用戶名和密碼不能為空");
        }
        
        Employee employee = authService.login(username, password);
        
        if (employee == null) {
            return ResponseEntity.badRequest().body("用戶名或密碼錯誤");
        }
        
        String token = authService.createToken(employee.getUsername());
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("employee", employee);
        response.put("token", token);
        
        return ResponseEntity.ok().headers(headers).body(response);
    }
    
    /**
     * 處理登出請求
     * 
     * @return 登出結果
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        authService.logout();
        return ResponseEntity.ok("已成功登出");
    }
    
    /**
     * 獲取當前登入的員工信息
     * 
     * @return 當前員工信息
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentEmployee() {
        Employee employee = authService.getCurrentEmployee();
        
        if (employee == null) {
            return ResponseEntity.status(401).body("未登入");
        }
        
        return ResponseEntity.ok(employee);
    }
    
    /**
     * 檢查用戶是否已認證
     * 
     * @return 是否已認證
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> isAuthenticated() {
        return ResponseEntity.ok(authService.isAuthenticated());
    }
}