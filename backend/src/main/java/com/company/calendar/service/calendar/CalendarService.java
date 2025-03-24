package com.company.calendar.service.calendar;

import com.company.calendar.dto.EventDTO;
import com.company.calendar.enums.SecurityLevel;
import com.company.calendar.model.Employee;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 行事曆服務介面
 * <p>
 * 定義處理事件相關業務邏輯的服務介面，包括事件的CRUD操作和權限管理功能。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
public interface CalendarService {
    
    /**
     * 獲取所有事件
     * 
     * @return 所有事件的列表
     */
    List<EventDTO> getAllEvents();
    
    /**
     * 根據ID獲取事件
     * 
     * @param id 事件ID
     * @return 事件資料傳輸物件，如果未找到則返回null
     */
    EventDTO getEventById(Long id);
    
    /**
     * 創建新事件
     * 
     * @param eventDTO 事件資料傳輸物件
     * @param creatorId 創建者ID
     * @return 創建的事件資料傳輸物件
     */
    EventDTO createEvent(EventDTO eventDTO, Long creatorId);
    
    /**
     * 更新事件信息
     * 
     * @param id 事件ID
     * @param eventDTO 事件資料傳輸物件
     * @return 更新後的事件資料傳輸物件
     */
    EventDTO updateEvent(Long id, EventDTO eventDTO);
    
    /**
     * 刪除事件
     * 
     * @param id 事件ID
     * @return 是否刪除成功
     */
    boolean deleteEvent(Long id);
    
    /**
     * 獲取指定時間區間內的事件
     * 
     * @param start 開始時間
     * @param end 結束時間
     * @return 時間區間內的事件列表
     */
    List<EventDTO> getEventsBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 獲取指定年月的事件
     * 
     * @param year 年份
     * @param month 月份（1-12）
     * @return 指定年月的事件列表
     */
    List<EventDTO> getEventsByYearAndMonth(int year, int month);
    
    /**
     * 獲取指定員工有權限查看的事件
     * 
     * @param employeeId 員工ID
     * @param start 開始時間
     * @param end 結束時間
     * @return 員工有權限查看的事件列表
     */
    List<EventDTO> getVisibleEventsForEmployee(Long employeeId, LocalDateTime start, LocalDateTime end);
    
    /**
     * 獲取指定創建者創建的事件
     * 
     * @param creatorId 創建者ID
     * @return 創建者創建的事件列表
     */
    List<EventDTO> getEventsByCreator(Long creatorId);
    
    /**
     * 搜索事件
     * 
     * @param keyword 關鍵字
     * @return 匹配的事件列表
     */
    List<EventDTO> searchEvents(String keyword);
    
    /**
     * 添加事件查看權限
     * 
     * @param eventId 事件ID
     * @param employeeId 員工ID
     * @return 是否添加成功
     */
    boolean addViewPermission(Long eventId, Long employeeId);
    
    /**
     * 移除事件查看權限
     * 
     * @param eventId 事件ID
     * @param employeeId 員工ID
     * @return 是否移除成功
     */
    boolean removeViewPermission(Long eventId, Long employeeId);
    
    /**
     * 獲取事件的特殊查看權限員工列表
     * 
     * @param eventId 事件ID
     * @return 有特殊查看權限的員工ID集合
     */
    Set<Long> getSpecialViewerIds(Long eventId);
    
    /**
     * 檢查員工是否有權限查看事件
     * 
     * @param eventId 事件ID
     * @param employeeId 員工ID
     * @return 是否有權限查看
     */
    boolean canEmployeeViewEvent(Long eventId, Long employeeId);
}