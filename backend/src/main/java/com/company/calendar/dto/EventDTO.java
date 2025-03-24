package com.company.calendar.dto;

import com.company.calendar.enums.SecurityLevel;
import com.company.calendar.model.Event;
import com.company.calendar.model.Employee;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 事件資料傳輸物件
 * <p>
 * 用於在前端和後端之間傳輸事件資料，避免循環引用問題，同時簡化序列化過程。
 * 包含事件的基本信息、時間、地點、創建者以及特殊查看權限等。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
public class EventDTO {

    /**
     * 事件ID
     */
    private Long id;
    
    /**
     * 事件標題
     */
    private String title;
    
    /**
     * 事件描述
     */
    private String description;
    
    /**
     * 事件開始時間
     */
    private LocalDateTime startTime;
    
    /**
     * 事件結束時間
     */
    private LocalDateTime endTime;
    
    /**
     * 事件地點
     */
    private String location;
    
    /**
     * 事件創建者ID
     */
    private Long creatorId;
    
    /**
     * 事件創建者用戶名
     */
    private String creatorUsername;
    
    /**
     * 事件安全等級
     */
    private SecurityLevel securityLevel;
    
    /**
     * 特殊查看權限的員工ID集合
     */
    private Set<Long> specialViewerIds = new HashSet<>();
    
    /**
     * 事件創建時間
     */
    private LocalDateTime createdAt;
    
    /**
     * 事件更新時間
     */
    private LocalDateTime updatedAt;
    
    /**
     * 預設建構子
     */
    public EventDTO() {
    }
    
    /**
     * 從事件實體創建DTO的建構子
     * 
     * @param event 事件實體
     */
    public EventDTO(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.startTime = event.getStartTime();
        this.endTime = event.getEndTime();
        this.location = event.getLocation();
        this.securityLevel = event.getSecurityLevel();
        this.createdAt = event.getCreatedAt();
        this.updatedAt = event.getUpdatedAt();
        
        if (event.getCreator() != null) {
            this.creatorId = event.getCreator().getId();
            this.creatorUsername = event.getCreator().getUsername();
        }
        
        this.specialViewerIds = event.getViewPermissions().stream()
                                     .map(permission -> permission.getEmployee().getId())
                                     .collect(Collectors.toSet());
    }
    
    /**
     * 將DTO轉換為事件實體，不包含關聯實體
     * 
     * @return 事件實體
     */
    public Event toEntity() {
        Event event = new Event();
        event.setId(this.id);
        event.setTitle(this.title);
        event.setDescription(this.description);
        event.setStartTime(this.startTime);
        event.setEndTime(this.endTime);
        event.setLocation(this.location);
        event.setSecurityLevel(this.securityLevel);
        event.setCreatedAt(this.createdAt);
        event.setUpdatedAt(this.updatedAt);
        return event;
    }
    
    /**
     * 將DTO轉換為事件實體，包含創建者實體
     * 
     * @param creator 創建者實體
     * @return 完整的事件實體
     */
    public Event toEntity(Employee creator) {
        Event event = toEntity();
        event.setCreator(creator);
        return event;
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
     * 獲取事件創建者ID
     * 
     * @return 事件創建者ID
     */
    public Long getCreatorId() {
        return creatorId;
    }

    /**
     * 設置事件創建者ID
     * 
     * @param creatorId 事件創建者ID
     */
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * 獲取事件創建者用戶名
     * 
     * @return 事件創建者用戶名
     */
    public String getCreatorUsername() {
        return creatorUsername;
    }

    /**
     * 設置事件創建者用戶名
     * 
     * @param creatorUsername 事件創建者用戶名
     */
    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
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
     * 獲取特殊查看權限的員工ID集合
     * 
     * @return 特殊查看權限的員工ID集合
     */
    public Set<Long> getSpecialViewerIds() {
        return specialViewerIds;
    }

    /**
     * 設置特殊查看權限的員工ID集合
     * 
     * @param specialViewerIds 特殊查看權限的員工ID集合
     */
    public void setSpecialViewerIds(Set<Long> specialViewerIds) {
        this.specialViewerIds = specialViewerIds;
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
     * 獲取事件更新時間
     * 
     * @return 事件更新時間
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 設置事件更新時間
     * 
     * @param updatedAt 事件更新時間
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}