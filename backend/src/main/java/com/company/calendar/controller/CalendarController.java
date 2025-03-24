package com.company.calendar.controller;

import com.company.calendar.dto.EventDTO;
import com.company.calendar.service.auth.AuthService;
import com.company.calendar.service.calendar.CalendarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

/**
 * 行事曆控制器
 * <p>
 * 處理行事曆事件相關的HTTP請求，包括事件的CRUD操作和權限管理功能。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    /**
     * 行事曆服務
     */
    private final CalendarService calendarService;
    
    /**
     * 認證服務
     */
    private final AuthService authService;
    
    /**
     * 構造函數
     * 
     * @param calendarService 行事曆服務
     * @param authService 認證服務
     */
    @Autowired
    public CalendarController(CalendarService calendarService, AuthService authService) {
        this.calendarService = calendarService;
        this.authService = authService;
    }
    
    /**
     * 獲取所有事件
     * 
     * @return 所有事件的列表
     */
    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = calendarService.getAllEvents();
        return ResponseEntity.ok(events);
    }
    
    /**
     * 根據ID獲取事件
     * 
     * @param id 事件ID
     * @return 事件信息
     */
    @GetMapping("/events/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        EventDTO event = calendarService.getEventById(id);
        
        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("事件不存在");
        }
        
        return ResponseEntity.ok(event);
    }
    
    /**
     * 創建新事件
     * 
     * @param eventDTO 事件資料傳輸物件
     * @return 創建的事件信息
     */
    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@RequestBody EventDTO eventDTO) {
        try {
            Long creatorId = authService.getCurrentEmployee().getId();
            EventDTO createdEvent = calendarService.createEvent(eventDTO, creatorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 更新事件信息
     * 
     * @param id 事件ID
     * @param eventDTO 事件資料傳輸物件
     * @return 更新後的事件信息
     */
    @PutMapping("/events/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        try {
            EventDTO updatedEvent = calendarService.updateEvent(id, eventDTO);
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 刪除事件
     * 
     * @param id 事件ID
     * @return 刪除結果
     */
    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        boolean success = calendarService.deleteEvent(id);
        
        if (success) {
            return ResponseEntity.ok().body("事件刪除成功");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("事件不存在");
        }
    }
    
    /**
     * 獲取指定時間區間內的事件
     * 
     * @param start 開始時間
     * @param end 結束時間
     * @return 時間區間內的事件列表
     */
    @GetMapping("/events/between")
    public ResponseEntity<List<EventDTO>> getEventsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<EventDTO> events = calendarService.getEventsBetween(start, end);
        return ResponseEntity.ok(events);
    }
    
    /**
     * 獲取指定年月的事件
     * 
     * @param year 年份
     * @param month 月份（1-12）
     * @return 指定年月的事件列表
     */
    @GetMapping("/events/year/{year}/month/{month}")
    public ResponseEntity<List<EventDTO>> getEventsByYearAndMonth(
            @PathVariable int year, @PathVariable int month) {
        // 驗證月份範圍
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().body(null);
        }
        
        List<EventDTO> events = calendarService.getEventsByYearAndMonth(year, month);
        return ResponseEntity.ok(events);
    }
    
    /**
     * 獲取當前登入員工有權限查看的當月事件
     * 
     * @param year 年份
     * @param month 月份（1-12）
     * @return 員工有權限查看的事件列表
     */
    @GetMapping("/events/visible/year/{year}/month/{month}")
    public ResponseEntity<?> getVisibleEventsForCurrentEmployee(
            @PathVariable int year, @PathVariable int month) {
        // 驗證月份範圍
        if (month < 1 || month > 12) {
            return ResponseEntity.badRequest().body("無效的月份");
        }
        
        // 獲取當前員工ID
        Long employeeId = authService.getCurrentEmployee().getId();
        
        // 計算開始和結束時間
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        
        List<EventDTO> events = calendarService.getVisibleEventsForEmployee(employeeId, start, end);
        return ResponseEntity.ok(events);
    }
    
    /**
     * 獲取當前登入員工創建的事件
     * 
     * @return 創建者創建的事件列表
     */
    @GetMapping("/events/my")
    public ResponseEntity<?> getMyEvents() {
        Long employeeId = authService.getCurrentEmployee().getId();
        List<EventDTO> events = calendarService.getEventsByCreator(employeeId);
        return ResponseEntity.ok(events);
    }
    
    /**
     * 搜索事件
     * 
     * @param keyword 關鍵字
     * @return 匹配的事件列表
     */
    @GetMapping("/events/search")
    public ResponseEntity<List<EventDTO>> searchEvents(@RequestParam String keyword) {
        List<EventDTO> events = calendarService.searchEvents(keyword);
        return ResponseEntity.ok(events);
    }
    
    /**
     * 添加事件查看權限
     * 
     * @param eventId 事件ID
     * @param employeeId 員工ID
     * @return 添加結果
     */
    @PostMapping("/events/{eventId}/permissions/{employeeId}")
    public ResponseEntity<?> addViewPermission(
            @PathVariable Long eventId, @PathVariable Long employeeId) {
        boolean success = calendarService.addViewPermission(eventId, employeeId);
        
        if (success) {
            return ResponseEntity.ok().body("權限添加成功");
        } else {
            return ResponseEntity.badRequest().body("權限添加失敗");
        }
    }
    
    /**
     * 移除事件查看權限
     * 
     * @param eventId 事件ID
     * @param employeeId 員工ID
     * @return 移除結果
     */
    @DeleteMapping("/events/{eventId}/permissions/{employeeId}")
    public ResponseEntity<?> removeViewPermission(
            @PathVariable Long eventId, @PathVariable Long employeeId) {
        boolean success = calendarService.removeViewPermission(eventId, employeeId);
        
        if (success) {
            return ResponseEntity.ok().body("權限移除成功");
        } else {
            return ResponseEntity.badRequest().body("權限移除失敗");
        }
    }
    
    /**
     * 獲取事件的特殊查看權限員工列表
     * 
     * @param eventId 事件ID
     * @return 有特殊查看權限的員工ID集合
     */
    @GetMapping("/events/{eventId}/permissions")
    public ResponseEntity<Set<Long>> getSpecialViewerIds(@PathVariable Long eventId) {
        Set<Long> viewerIds = calendarService.getSpecialViewerIds(eventId);
        return ResponseEntity.ok(viewerIds);
    }
    
    /**
     * 檢查員工是否有權限查看事件
     * 
     * @param eventId 事件ID
     * @return 是否有權限查看
     */
    @GetMapping("/events/{eventId}/can-view")
    public ResponseEntity<?> canCurrentEmployeeViewEvent(@PathVariable Long eventId) {
        Long employeeId = authService.getCurrentEmployee().getId();
        boolean canView = calendarService.canEmployeeViewEvent(eventId, employeeId);
        
        return ResponseEntity.ok().body(canView);
    }
}