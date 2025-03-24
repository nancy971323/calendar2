package com.company.calendar.dto;

/**
 * 登入資料傳輸物件
 * <p>
 * 用於處理用戶登入請求，封裝了登入需要的用戶名和密碼信息。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
public class LoginDTO {

    /**
     * 用戶名
     */
    private String username;
    
    /**
     * 密碼
     */
    private String password;
    
    /**
     * 預設建構子
     */
    public LoginDTO() {
    }
    
    /**
     * 建構子
     * 
     * @param username 用戶名
     * @param password 密碼
     */
    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    /**
     * 獲取用戶名
     * 
     * @return 用戶名
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * 設置用戶名
     * 
     * @param username 用戶名
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * 獲取密碼
     * 
     * @return 密碼
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * 設置密碼
     * 
     * @param password 密碼
     */
    public void setPassword(String password) {
        this.password = password;
    }
}