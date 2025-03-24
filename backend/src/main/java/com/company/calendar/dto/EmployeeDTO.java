package com.company.calendar.dto;

import com.company.calendar.enums.SecurityLevel;
import com.company.calendar.model.Employee;

import java.time.LocalDateTime;

/**
 * 員工資料傳輸物件
 * <p>
 * 用於在前端和後端之間傳輸員工資料，避免暴露敏感信息如密碼等。
 * 同時簡化了對員工實體的序列化過程，避免了循環引用問題。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
public class EmployeeDTO {

    /**
     * 員工ID
     */
    private Long id;
    
    /**
     * 員工用戶名
     */
    private String username;
    
    /**
     * 員工全名
     */
    private String fullName;
    
    /**
     * 員工電子郵件
     */
    private String email;
    
    /**
     * 員工部門
     */
    private String department;
    
    /**
     * 員工安全等級
     */
    private SecurityLevel securityLevel;
    
    /**
     * 員工帳號創建時間
     */
    private LocalDateTime createdAt;
    
    /**
     * 員工帳號更新時間
     */
    private LocalDateTime updatedAt;
    
    /**
     * 員工帳號是否啟用
     */
    private boolean active;
    
    /**
     * 預設建構子
     */
    public EmployeeDTO() {
    }
    
    /**
     * 從員工實體創建DTO的建構子
     * 
     * @param employee 員工實體
     */
    public EmployeeDTO(Employee employee) {
        this.id = employee.getId();
        this.username = employee.getUsername();
        this.fullName = employee.getFullName();
        this.email = employee.getEmail();
        this.department = employee.getDepartment();
        this.securityLevel = employee.getSecurityLevel();
        this.createdAt = employee.getCreatedAt();
        this.updatedAt = employee.getUpdatedAt();
        this.active = employee.isActive();
    }
    
    /**
     * 將DTO轉換為員工實體
     * 
     * @return 員工實體
     */
    public Employee toEntity() {
        Employee employee = new Employee();
        employee.setId(this.id);
        employee.setUsername(this.username);
        employee.setFullName(this.fullName);
        employee.setEmail(this.email);
        employee.setDepartment(this.department);
        employee.setSecurityLevel(this.securityLevel);
        employee.setCreatedAt(this.createdAt);
        employee.setUpdatedAt(this.updatedAt);
        employee.setActive(this.active);
        return employee;
    }

    // Getters and Setters
    
    /**
     * 獲取員工ID
     * 
     * @return 員工ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 設置員工ID
     * 
     * @param id 員工ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 獲取員工用戶名
     * 
     * @return 員工用戶名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 設置員工用戶名
     * 
     * @param username 員工用戶名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 獲取員工全名
     * 
     * @return 員工全名
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * 設置員工全名
     * 
     * @param fullName 員工全名
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * 獲取員工電子郵件
     * 
     * @return 員工電子郵件
     */
    public String getEmail() {
        return email;
    }

    /**
     * 設置員工電子郵件
     * 
     * @param email 員工電子郵件
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 獲取員工部門
     * 
     * @return 員工部門
     */
    public String getDepartment() {
        return department;
    }

    /**
     * 設置員工部門
     * 
     * @param department 員工部門
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * 獲取員工安全等級
     * 
     * @return 員工安全等級
     */
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    /**
     * 設置員工安全等級
     * 
     * @param securityLevel 員工安全等級
     */
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

    /**
     * 獲取員工帳號創建時間
     * 
     * @return 員工帳號創建時間
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 設置員工帳號創建時間
     * 
     * @param createdAt 員工帳號創建時間
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 獲取員工帳號更新時間
     * 
     * @return 員工帳號更新時間
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 設置員工帳號更新時間
     * 
     * @param updatedAt 員工帳號更新時間
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 獲取員工帳號是否啟用
     * 
     * @return 員工帳號是否啟用
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 設置員工帳號是否啟用
     * 
     * @param active 員工帳號是否啟用
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}