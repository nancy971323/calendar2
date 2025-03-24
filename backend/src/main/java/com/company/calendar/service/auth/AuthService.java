package com.company.calendar.service.auth;

import com.company.calendar.dto.EmployeeDTO;
import com.company.calendar.dto.LoginDTO;

/**
 * 認證服務介面
 * <p>
 * 定義處理用戶認證相關功能的服務介面，包括登入、註冊和獲取當前登入用戶等功能。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
public interface AuthService {
    
    /**
     * 用戶登入
     * 
     * @param loginDTO 登入資料傳輸物件，包含用戶名和密碼
     * @return 登入成功後的員工資料傳輸物件，如果登入失敗則返回null
     */
    EmployeeDTO login(LoginDTO loginDTO);
    
    /**
     * 獲取當前登入用戶
     * 
     * @return 當前登入用戶的員工資料傳輸物件，如果未登入則返回null
     */
    EmployeeDTO getCurrentEmployee();
    
    /**
     * 檢查用戶是否已登入
     * 
     * @return 如果已登入則返回true，否則返回false
     */
    boolean isAuthenticated();
    
    /**
     * 登出當前用戶
     */
    void logout();
    
    /**
     * 創建JWTToken
     * 
     * @param username 用戶名
     * @return JWT令牌
     */
    String createToken(String username);
    
    /**
     * 從JWT令牌中獲取用戶名
     * 
     * @param token JWT令牌
     * @return 用戶名
     */
    String getUsernameFromToken(String token);
    
    /**
     * 驗證JWT令牌是否有效
     * 
     * @param token JWT令牌
     * @return 如果有效則返回true，否則返回false
     */
    boolean validateToken(String token);
}