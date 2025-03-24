package com.company.calendar.model;

import com.company.calendar.enums.SecurityLevel;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 員工實體類
 * <p>
 * 代表系統中的員工用戶，包含員工基本信息以及權限等級。
 * 每個員工都有一個安全等級，決定其在系統中的操作權限及可見範圍。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Entity
@Table(name = "employees")
public class Employee {

    /**
     * 員工ID，主鍵
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 員工用戶名，用於登入系統
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * 員工密碼，存儲加密後的密碼
     */
    @Column(nullable = false)
    private String password;

    /**
     * 員工真實姓名
     */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /**
     * 員工電子郵件
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 員工部門
     */
    @Column
    private String department;

    /**
     * 員工安全等級，決定員工的權限範圍
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "security_level", nullable = false)
    private SecurityLevel securityLevel;

    /**
     * 員工建立的事件集合
     */
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Event> createdEvents = new HashSet<>();

    /**
     * 員工被特別許可可查看的事件集合
     */
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<EventViewPermission> eventViewPermissions = new HashSet<>();

    /**
     * 帳號創建時間
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 帳號最後更新時間
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 帳號是否啟用
     */
    @Column(nullable = false)
    private boolean active = true;

    /**
     * 預設建構子
     */
    public Employee() {
    }

    /**
     * 建構子
     * 
     * @param username 用戶名
     * @param password 密碼
     * @param fullName 全名
     * @param email 電子郵件
     * @param department 部門
     * @param securityLevel 安全等級
     */
    public Employee(String username, String password, String fullName, String email, 
                    String department, SecurityLevel securityLevel) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.securityLevel = securityLevel;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * 創建實體前的操作
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    /**
     * 更新實體前的操作
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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
     * 獲取員工密碼
     * 
     * @return 員工密碼
     */
    public String getPassword() {
        return password;
    }

    /**
     * 設置員工密碼
     * 
     * @param password 員工密碼
     */
    public void setPassword(String password) {
        this.password = password;
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
     * 獲取員工創建的事件集合
     * 
     * @return 員工創建的事件集合
     */
    public Set<Event> getCreatedEvents() {
        return createdEvents;
    }

    /**
     * 設置員工創建的事件集合
     * 
     * @param createdEvents 員工創建的事件集合
     */
    public void setCreatedEvents(Set<Event> createdEvents) {
        this.createdEvents = createdEvents;
    }

    /**
     * 獲取員工被特別許可可查看的事件集合
     * 
     * @return 員工被特別許可可查看的事件集合
     */
    public Set<EventViewPermission> getEventViewPermissions() {
        return eventViewPermissions;
    }

    /**
     * 設置員工被特別許可可查看的事件集合
     * 
     * @param eventViewPermissions 員工被特別許可可查看的事件集合
     */
    public void setEventViewPermissions(Set<EventViewPermission> eventViewPermissions) {
        this.eventViewPermissions = eventViewPermissions;
    }

    /**
     * 獲取帳號創建時間
     * 
     * @return 帳號創建時間
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 設置帳號創建時間
     * 
     * @param createdAt 帳號創建時間
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 獲取帳號最後更新時間
     * 
     * @return 帳號最後更新時間
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 設置帳號最後更新時間
     * 
     * @param updatedAt 帳號最後更新時間
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 獲取帳號是否啟用
     * 
     * @return 帳號是否啟用
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 設置帳號是否啟用
     * 
     * @param active 帳號是否啟用
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * 檢查員工是否有權限查看指定安全等級的事件
     * 
     * @param eventSecurityLevel 事件安全等級
     * @return 是否有權限查看
     */
    public boolean canAccessSecurityLevel(SecurityLevel eventSecurityLevel) {
        return this.securityLevel.hasAccessTo(eventSecurityLevel);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", department='" + department + '\'' +
                ", securityLevel=" + securityLevel +
                ", active=" + active +
                '}';
    }
}