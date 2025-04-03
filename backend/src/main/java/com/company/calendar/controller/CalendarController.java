package com.company.calendar.controller;

import com.company.calendar.model.Event;
import com.company.calendar.model.Employee;
import com.company.calendar.service.auth.AuthService;
import com.company.calendar.service.calendar.CalendarService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 行事曆控制器
 * <p>
 * 處理行事曆相關的HTTP請求，包括事件的CRUD操作和事件權限管理。
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
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = calendarService.getAllEvents();
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
        Event event = calendarService.getEventById(id);
        
        if (event == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("事件不存在");
        }
        
        return ResponseEntity.ok(event);
    }
    
    /**
     * 創建新事件
     * 
     * @param event 事件信息
     * @return 創建的事件信息
     */
    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        try {
            Employee creator = authService.getCurrentEmployee();
            event.setCreator(creator);
            Event createdEvent = calendarService.createEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 更新事件信息
     * 
     * @param id 事件ID
     * @param event 事件信息
     * @return 更新後的事件信息
     */
    @PutMapping("/events/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        if (!id.equals(event.getId())) {
            return ResponseEntity.badRequest().body("ID不匹配");
        }
        
        try {
            // 檢查當前用戶是否是事件創建者
            Employee currentEmployee = authService.getCurrentEmployee();
            Event existingEvent = calendarService.getEventById(id);
            
            if (existingEvent == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("事件不存在");
            }
            
            if (!existingEvent.getCreator().getId().equals(currentEmployee.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有事件創建者可以更新事件");
            }
            
            // 保持原創建者不變
            event.setCreator(existingEvent.getCreator());
            
            Event updatedEvent = calendarService.updateEvent(event);
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 刪除事件
     * 
     * @param id 事件ID
     * @return 操作結果
     */
    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        // 檢查當前用戶是否是事件創建者
        Employee currentEmployee = authService.getCurrentEmployee();
        Event existingEvent = calendarService.getEventById(id);
        
        if (existingEvent == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("事件不存在");
        }
        
        if (!existingEvent.getCreator().getId().equals(currentEmployee.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有事件創建者可以刪除事件");
        }
        
        boolean deleted = calendarService.deleteEvent(id);
        
        if (deleted) {
            return ResponseEntity.ok("事件已刪除");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("事件不存在");
        }
    }
    
    /**
     * 根據時間範圍獲取事件
     * 
     * @param start 開始時間
     * @param end 結束時間
     * @return 時間範圍內的事件列表
     */
    @GetMapping("/events/range")
    public ResponseEntity<List<Event>> getEventsByTimeRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Event> events = calendarService.getEventsByTimeRange(start, end);
        return ResponseEntity.ok(events);
    }
    
    /**
     * 獲取當前員工可見的所有事件
     * 
     * @return 當前員工可見的事件列表
     */
    @GetMapping("/events/visible")
    public ResponseEntity<List<Event>> getVisibleEvents() {
        Employee currentEmployee = authService.getCurrentEmployee();
        List<Event> events = calendarService.getEventsVisibleToEmployee(currentEmployee.getId());
        return ResponseEntity.ok(events);
    }
    
    /**
     * 獲取當前員工可見的時間範圍內的事件
     * 
     * @param start 開始時間
     * @param end 結束時間
     * @return 當前員工可見的時間範圍內的事件列表
     */
    @GetMapping("/events/visible/range")
    public ResponseEntity<List<Event>> getVisibleEventsByTimeRange(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        Employee currentEmployee = authService.getCurrentEmployee();
        List<Event> events = calendarService.getEventsVisibleToEmployeeInTimeRange(
                currentEmployee.getId(), start, end);
        return ResponseEntity.ok(events);
    }
    
    /**
     * 獲取當前員工指定年月可見的事件
     * 
     * @param year 年份
     * @param month 月份（1-12）
     * @return 當前員工指定年月可見的事件列表
     */
    @GetMapping("/events/visible/year/{year}/month/{month}")
    public ResponseEntity<List<Event>> getVisibleEventsByYearMonth(
            @PathVariable int year, 
            @PathVariable int month) {
        Employee currentEmployee = authService.getCurrentEmployee();
        if (currentEmployee == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        
        // 構建該月份的開始和結束時間
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        
        List<Event> events = calendarService.getEventsVisibleToEmployeeInTimeRange(
                currentEmployee.getId(), startOfMonth, endOfMonth);
        return ResponseEntity.ok(events);
    }
    
    /**
     * 獲取當前員工創建的所有事件
     * 
     * @return 當前員工創建的事件列表
     */
    @GetMapping("/events/created")
    public ResponseEntity<List<Event>> getCreatedEvents() {
        Employee currentEmployee = authService.getCurrentEmployee();
        List<Event> events = calendarService.getEventsByCreator(currentEmployee.getId());
        return ResponseEntity.ok(events);
    }
    
    /**
     * 為事件添加特殊查看權限
     * 
     * @param eventId 事件ID
     * @param request 包含員工ID的請求
     * @return 操作結果
     */
    @PostMapping("/events/{eventId}/permissions")
    public ResponseEntity<?> addViewPermission(
            @PathVariable Long eventId,
            @RequestBody Map<String, Long> request) {
        Long employeeId = request.get("employeeId");
        
        if (employeeId == null) {
            return ResponseEntity.badRequest().body("缺少必要參數");
        }
        
        // 檢查當前用戶是否是事件創建者
        Employee currentEmployee = authService.getCurrentEmployee();
        Event existingEvent = calendarService.getEventById(eventId);
        
        if (existingEvent == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("事件不存在");
        }
        
        if (!existingEvent.getCreator().getId().equals(currentEmployee.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有事件創建者可以管理權限");
        }
        
        boolean added = calendarService.addViewPermission(eventId, employeeId);
        
        if (added) {
            return ResponseEntity.ok("已添加查看權限");
        } else {
            return ResponseEntity.badRequest().body("添加查看權限失敗");
        }
    }
    
    /**
     * 移除事件的特殊查看權限
     * 
     * @param eventId 事件ID
     * @param employeeId 員工ID
     * @return 操作結果
     */
    @DeleteMapping("/events/{eventId}/permissions/{employeeId}")
    public ResponseEntity<?> removeViewPermission(
            @PathVariable Long eventId,
            @PathVariable Long employeeId) {
        // 檢查當前用戶是否是事件創建者
        Employee currentEmployee = authService.getCurrentEmployee();
        Event existingEvent = calendarService.getEventById(eventId);
        
        if (existingEvent == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("事件不存在");
        }
        
        if (!existingEvent.getCreator().getId().equals(currentEmployee.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("只有事件創建者可以管理權限");
        }
        
        boolean removed = calendarService.removeViewPermission(eventId, employeeId);
        
        if (removed) {
            return ResponseEntity.ok("已移除查看權限");
        } else {
            return ResponseEntity.badRequest().body("移除查看權限失敗");
        }
    }
    
    /**
     * 獲取有特殊查看權限的員工ID列表
     * 
     * @param eventId 事件ID
     * @return 有特殊查看權限的員工ID集合
     */
    @GetMapping("/events/{eventId}/permissions")
    public ResponseEntity<?> getSpecialViewers(@PathVariable Long eventId) {
        // 檢查事件是否存在
        Event existingEvent = calendarService.getEventById(eventId);
        
        if (existingEvent == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("事件不存在");
        }
        
        Set<Long> viewerIds = calendarService.getSpecialViewerIds(eventId);
        return ResponseEntity.ok(viewerIds);
    }
    
    /**
     * 檢查當前員工是否可以查看特定事件
     * 
     * @param eventId 事件ID
     * @return 是否可以查看
     */
    @GetMapping("/events/{eventId}/can-view")
    public ResponseEntity<?> canCurrentEmployeeViewEvent(@PathVariable Long eventId) {
        Employee currentEmployee = authService.getCurrentEmployee();
        boolean canView = calendarService.canEmployeeViewEvent(eventId, currentEmployee.getId());
        
        return ResponseEntity.ok(canView);
    }
}