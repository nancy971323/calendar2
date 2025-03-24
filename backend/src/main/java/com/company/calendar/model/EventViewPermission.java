package com.company.calendar.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 事件查看權限實體類
 * <p>
 * 表示特定的員工對特定事件的查看權限，主要用於允許低權限等級的員工查看高權限等級的事件。
 * 此實體實現了多對多關係中的額外屬性。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Entity
@Table(name = "event_view_permissions")
public class EventViewPermission {

    /**
     * 權限ID，主鍵
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 關聯的事件
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * 被授予權限的員工
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    /**
     * 權限授予時間
     */
    @Column(name = "granted_at")
    private LocalDateTime grantedAt;

    /**
     * 授予權限的員工（通常是事件創建者）
     */
    @Column(name = "granted_by")
    private String grantedBy;

    /**
     * 預設建構子
     */
    public EventViewPermission() {
    }

    /**
     * 建構子
     * 
     * @param event 關聯的事件
     * @param employee 被授予權限的員工
     */
    public EventViewPermission(Event event, Employee employee) {
        this.event = event;
        this.employee = employee;
        this.grantedAt = LocalDateTime.now();
    }

    /**
     * 建構子
     * 
     * @param event 關聯的事件
     * @param employee 被授予權限的員工
     * @param grantedBy 授予權限的員工
     */
    public EventViewPermission(Event event, Employee employee, String grantedBy) {
        this.event = event;
        this.employee = employee;
        this.grantedAt = LocalDateTime.now();
        this.grantedBy = grantedBy;
    }

    /**
     * 創建實體前的操作
     */
    @PrePersist
    protected void onCreate() {
        grantedAt = LocalDateTime.now();
    }

    // Getters and Setters

    /**
     * 獲取權限ID
     * 
     * @return 權限ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 設置權限ID
     * 
     * @param id 權限ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 獲取關聯的事件
     * 
     * @return 關聯的事件
     */
    public Event getEvent() {
        return event;
    }

    /**
     * 設置關聯的事件
     * 
     * @param event 關聯的事件
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * 獲取被授予權限的員工
     * 
     * @return 被授予權限的員工
     */
    public Employee getEmployee() {
        return employee;
    }

    /**
     * 設置被授予權限的員工
     * 
     * @param employee 被授予權限的員工
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    /**
     * 獲取權限授予時間
     * 
     * @return 權限授予時間
     */
    public LocalDateTime getGrantedAt() {
        return grantedAt;
    }

    /**
     * 設置權限授予時間
     * 
     * @param grantedAt 權限授予時間
     */
    public void setGrantedAt(LocalDateTime grantedAt) {
        this.grantedAt = grantedAt;
    }

    /**
     * 獲取授予權限的員工
     * 
     * @return 授予權限的員工
     */
    public String getGrantedBy() {
        return grantedBy;
    }

    /**
     * 設置授予權限的員工
     * 
     * @param grantedBy 授予權限的員工
     */
    public void setGrantedBy(String grantedBy) {
        this.grantedBy = grantedBy;
    }

    @Override
    public String toString() {
        return "EventViewPermission{" +
                "id=" + id +
                ", event=" + event.getId() +
                ", employee=" + employee.getUsername() +
                ", grantedAt=" + grantedAt +
                ", grantedBy='" + grantedBy + '\'' +
                '}';
    }
}