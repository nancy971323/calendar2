package com.company.calendar.controller;

import com.company.calendar.dto.EmployeeDTO;
import com.company.calendar.dto.LoginDTO;
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
     * @param loginDTO 登入資料傳輸物件
     * @return 登入結果，包含用戶信息和JWT令牌
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        EmployeeDTO employee = authService.login(loginDTO);
        
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
    public ResponseEntity<?> logout() {
        authService.logout();
        return ResponseEntity.ok().body("登出成功");
    }
    
    /**
     * 獲取當前登入用戶信息
     * 
     * @return 當前用戶信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        EmployeeDTO employee = authService.getCurrentEmployee();
        
        if (employee == null) {
            return ResponseEntity.status(401).body("未登入");
        }
        
        return ResponseEntity.ok().body(employee);
    }
    
    /**
     * 檢查用戶是否已登入
     * 
     * @return 登入狀態
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkAuthentication() {
        boolean isAuthenticated = authService.isAuthenticated();
        
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", isAuthenticated);
        
        return ResponseEntity.ok().body(response);
    }
}