package com.company.calendar.model;

import com.company.calendar.enums.SecurityLevel;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 事件實體類
 * <p>
 * 代表行事曆系統中的事件，包含事件基本信息、時間、地點、創建者、安全等級等。
 * 每個事件都有一個安全等級，決定哪些員工可以查看該事件。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Entity
@Table(name = "events")
public class Event {

    /**
     * 事件ID，主鍵
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 事件標題
     */
    @Column(nullable = false)
    private String title;

    /**
     * 事件描述
     */
    @Column(length = 1000)
    private String description;

    /**
     * 事件開始時間
     */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    /**
     * 事件結束時間
     */
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /**
     * 事件地點
     */
    @Column
    private String location;

    /**
     * L事件創建者
     */
    @JsonBackReference("employee-events")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Employee creator;

    /**
     * 事件安全等級，決定事件的可見範圍
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "security_level", nullable = false)
    private SecurityLevel securityLevel;

    /**
     * 特殊權限設定，允許低權限員工查看此事件
     */
    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<EventViewPermission> viewPermissions = new HashSet<>();

    /**
     * 事件創建時間
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 事件最後更新時間
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 預設建構子
     */
    public Event() {
    }

    /**
     * 建構子
     * 
     * @param title 事件標題
     * @param description 事件描述
     * @param startTime 開始時間
     * @param endTime 結束時間
     * @param location 地點
     * @param creator 創建者
     * @param securityLevel 安全等級
     */
    public Event(String title, String description, LocalDateTime startTime, LocalDateTime endTime,
                 String location, Employee creator, SecurityLevel securityLevel) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.creator = creator;
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
     * 獲取事件ID
     * 
     * @return 事件ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 設置事件ID
     * 
     * @param id 事件ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 獲取事件標題
     * 
     * @return 事件標題
     */
    public String getTitle() {
        return title;
    }

    /**
     * 設置事件標題
     * 
     * @param title 事件標題
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 獲取事件描述
     * 
     * @return 事件描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 設置事件描述
     * 
     * @param description 事件描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 獲取事件開始時間
     * 
     * @return 事件開始時間
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * 設置事件開始時間
     * 
     * @param startTime 事件開始時間
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * 獲取事件結束時間
     * 
     * @return 事件結束時間
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * 設置事件結束時間
     * 
     * @param endTime 事件結束時間
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * 獲取事件地點
     * 
     * @return 事件地點
     */
    public String getLocation() {
        return location;
    }

    /**
     * 設置事件地點
     * 
     * @param location 事件地點
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 獲取事件創建者
     * 
     * @return 事件創建者
     */
    public Employee getCreator() {
        return creator;
    }

    /**
     * 設置事件創建者
     * 
     * @param creator 事件創建者
     */
    public void setCreator(Employee creator) {
        this.creator = creator;
    }

    /**
     * 獲取事件安全等級
     * 
     * @return 事件安全等級
     */
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    /**
     * 設置事件安全等級
     * 
     * @param securityLevel 事件安全等級
     */
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }

    /**
     * 獲取事件特殊查看權限集合
     * 
     * @return 事件特殊查看權限集合
     */
    public Set<EventViewPermission> getViewPermissions() {
        return viewPermissions;
    }

    /**
     * 設置事件特殊查看權限集合
     * 
     * @param viewPermissions 事件特殊查看權限集合
     */
    public void setViewPermissions(Set<EventViewPermission> viewPermissions) {
        this.viewPermissions = viewPermissions;
    }

    /**
     * 獲取事件創建時間
     * 
     * @return 事件創建時間
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 設置事件創建時間
     * 
     * @param createdAt 事件創建時間
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 獲取事件最後更新時間
     * 
     * @return 事件最後更新時間
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 設置事件最後更新時間
     * 
     * @param updatedAt 事件最後更新時間
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * 添加特殊查看權限
     * 
     * @param employee 被授權的員工
     */
    public void addViewPermission(Employee employee) {
        EventViewPermission permission = new EventViewPermission(this, employee);
        viewPermissions.add(permission);
        employee.getEventViewPermissions().add(permission);
    }

    /**
     * 移除特殊查看權限
     * 
     * @param employee 被移除權限的員工
     */
    public void removeViewPermission(Employee employee) {
        for (EventViewPermission permission : viewPermissions) {
            if (permission.getEmployee().equals(employee)) {
                employee.getEventViewPermissions().remove(permission);
                viewPermissions.remove(permission);
                break;
            }
        }
    }

    /**
     * 檢查指定員工是否有權限查看此事件
     * 
     * @param employee 要檢查的員工
     * @return 是否有權限查看
     */
    public boolean canBeViewedBy(Employee employee) {
        // 事件創建者始終可見
        if (creator.equals(employee)) {
            return true;
        }
        
        // 檢查員工權限等級
        if (employee.canAccessSecurityLevel(securityLevel)) {
            return true;
        }
        
        // 檢查特殊權限
        for (EventViewPermission permission : viewPermissions) {
            if (permission.getEmployee().equals(employee)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location='" + location + '\'' +
                ", securityLevel=" + securityLevel +
                '}';
    }
}