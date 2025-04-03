package com.company.calendar.service.calendar;

import com.company.calendar.enums.SecurityLevel;
import com.company.calendar.model.Employee;
import com.company.calendar.model.Event;
import com.company.calendar.model.EventViewPermission;
import com.company.calendar.repository.EmployeeRepository;
import com.company.calendar.repository.EventRepository;
import com.company.calendar.repository.EventViewPermissionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 行事曆服務類
 * <p>
 * 提供行事曆相關業務邏輯的具體實現，包括事件的CRUD操作和權限管理。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Service
public class CalendarService {

    /**
     * 事件資料庫操作介面
     */
    private final EventRepository eventRepository;
    
    /**
     * 員工資料庫操作介面
     */
    private final EmployeeRepository employeeRepository;
    
    /**
     * 事件查看權限資料庫操作介面
     */
    private final EventViewPermissionRepository eventViewPermissionRepository;
    
    /**
     * 構造函數
     * 
     * @param eventRepository 事件資料庫操作介面
     * @param employeeRepository 員工資料庫操作介面
     * @param eventViewPermissionRepository 事件查看權限資料庫操作介面
     */
    @Autowired
    public CalendarService(EventRepository eventRepository,
                           EmployeeRepository employeeRepository,
                           EventViewPermissionRepository eventViewPermissionRepository) {
        this.eventRepository = eventRepository;
        this.employeeRepository = employeeRepository;
        this.eventViewPermissionRepository = eventViewPermissionRepository;
    }
    
    /**
     * 獲取所有事件
     * 
     * @return 所有事件的列表
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    
    /**
     * 根據ID獲取事件
     * 
     * @param id 事件ID
     * @return 事件信息，如果不存在則返回null
     */
    public Event getEventById(Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        return eventOpt.orElse(null);
    }
    
    /**
     * 創建新事件
     * 
     * @param event 事件信息（包含創建者）
     * @return 創建的事件信息
     * @throws IllegalArgumentException 參數無效時拋出
     */
    @Transactional
    public Event createEvent(Event event) throws IllegalArgumentException {
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("事件標題不能為空");
        }
        
        if (event.getStartTime() == null || event.getEndTime() == null) {
            throw new IllegalArgumentException("事件開始和結束時間不能為空");
        }
        
        if (event.getStartTime().isAfter(event.getEndTime())) {
            throw new IllegalArgumentException("事件開始時間不能晚於結束時間");
        }
        
        if (event.getCreator() == null || event.getCreator().getId() == null) {
            throw new IllegalArgumentException("事件必須指定創建者");
        }
        
        // 設置時間
        LocalDateTime now = LocalDateTime.now();
        event.setCreatedAt(now);
        event.setUpdatedAt(now);
        
        // 設置默認安全等級
        if (event.getSecurityLevel() == null) {
            event.setSecurityLevel(SecurityLevel.LEVEL_1);
        }
        
        // 保存事件
        return eventRepository.save(event);
    }
    
    /**
     * 更新事件信息
     * 
     * @param event 事件信息
     * @return 更新後的事件信息
     * @throws IllegalArgumentException 參數無效時拋出
     */
    @Transactional
    public Event updateEvent(Event event) throws IllegalArgumentException {
        if (event.getId() == null) {
            throw new IllegalArgumentException("事件ID不能為空");
        }
        
        Optional<Event> existingEventOpt = eventRepository.findById(event.getId());
        if (existingEventOpt.isEmpty()) {
            throw new IllegalArgumentException("事件不存在: " + event.getId());
        }
        
        Event existingEvent = existingEventOpt.get();
        
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("事件標題不能為空");
        }
        
        if (event.getStartTime() == null || event.getEndTime() == null) {
            throw new IllegalArgumentException("事件開始和結束時間不能為空");
        }
        
        if (event.getStartTime().isAfter(event.getEndTime())) {
            throw new IllegalArgumentException("事件開始時間不能晚於結束時間");
        }
        
        // 保留原創建者和創建時間
        event.setCreator(existingEvent.getCreator());
        event.setCreatedAt(existingEvent.getCreatedAt());
        
        // 更新時間
        event.setUpdatedAt(LocalDateTime.now());
        
        // 保留原查看權限
        event.setViewPermissions(existingEvent.getViewPermissions());
        
        // 保存更新
        return eventRepository.save(event);
    }
    
    /**
     * 刪除事件
     * 
     * @param id 事件ID
     * @return 是否成功刪除
     */
    @Transactional
    public boolean deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            return false;
        }
        
        // 刪除相關的權限記錄
        Event event = eventRepository.getReferenceById(id);
        eventViewPermissionRepository.deleteByEvent(event);
        
        // 刪除事件
        eventRepository.deleteById(id);
        
        return true;
    }
    
    /**
     * 根據時間範圍獲取事件
     * 
     * @param start 開始時間
     * @param end 結束時間
     * @return 時間範圍內的事件列表
     */
    public List<Event> getEventsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByStartTimeBetweenOrEndTimeBetween(start, end, start, end);
    }
    
    /**
     * 根據創建者獲取事件
     * 
     * @param creatorId 創建者ID
     * @return 創建者創建的事件列表
     */
    public List<Event> getEventsByCreator(Long creatorId) {
        Optional<Employee> creatorOpt = employeeRepository.findById(creatorId);
        if (creatorOpt.isEmpty()) {
            return new ArrayList<>();
        }
        
        return eventRepository.findByCreator(creatorOpt.get());
    }
    
    /**
     * 獲取員工可見的所有事件
     * 
     * @param employeeId 員工ID
     * @return 員工可見的事件列表
     */
    public List<Event> getEventsVisibleToEmployee(Long employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            return new ArrayList<>();
        }
        
        Employee employee = employeeOpt.get();
        
        // 獲取員工創建的事件
        List<Event> visibleEvents = new ArrayList<>(eventRepository.findByCreator(employee));
        
        // 獲取根據安全等級可見的事件
        List<Event> eventsVisibleBySecurity = eventRepository.findAll().stream()
                .filter(event -> employee.canAccessSecurityLevel(event.getSecurityLevel()))
                .collect(Collectors.toList());
        
        // 添加未包含的事件
        for (Event event : eventsVisibleBySecurity) {
            if (!visibleEvents.contains(event)) {
                visibleEvents.add(event);
            }
        }
        
        // 獲取特殊權限可見的事件
        List<EventViewPermission> permissions = eventViewPermissionRepository.findByEmployee(employee);
        for (EventViewPermission permission : permissions) {
            Event event = permission.getEvent();
            if (!visibleEvents.contains(event)) {
                visibleEvents.add(event);
            }
        }
        
        return visibleEvents;
    }
    
    /**
     * 獲取員工可見的時間範圍內的事件
     * 
     * @param employeeId 員工ID
     * @param start 開始時間
     * @param end 結束時間
     * @return 員工可見的時間範圍內的事件列表
     */
    public List<Event> getEventsVisibleToEmployeeInTimeRange(Long employeeId, LocalDateTime start, LocalDateTime end) {
        return getEventsVisibleToEmployee(employeeId).stream()
                .filter(event -> {
                    // 檢查事件是否在指定時間範圍內
                    return (event.getStartTime().isEqual(start) || event.getStartTime().isAfter(start) ||
                            event.getEndTime().isEqual(start) || event.getEndTime().isAfter(start)) &&
                           (event.getStartTime().isEqual(end) || event.getStartTime().isBefore(end) ||
                            event.getEndTime().isEqual(end) || event.getEndTime().isBefore(end));
                })
                .collect(Collectors.toList());
    }
    
    /**
     * 檢查員工是否可以查看事件
     * 
     * @param eventId 事件ID
     * @param employeeId 員工ID
     * @return 是否可以查看
     */
    public boolean canEmployeeViewEvent(Long eventId, Long employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        
        if (employeeOpt.isEmpty() || eventOpt.isEmpty()) {
            return false;
        }
        
        Employee employee = employeeOpt.get();
        Event event = eventOpt.get();
        
        // 檢查是否為創建者
        if (event.getCreator().getId().equals(employeeId)) {
            return true;
        }
        
        // 檢查安全等級
        if (employee.canAccessSecurityLevel(event.getSecurityLevel())) {
            return true;
        }
        
        // 檢查特殊權限
        return eventViewPermissionRepository.existsByEmployeeAndEvent(employee, event);
    }
    
    /**
     * 添加事件查看權限
     * 
     * @param eventId 事件ID
     * @param employeeId 員工ID
     * @return 是否成功添加
     */
    @Transactional
    public boolean addViewPermission(Long eventId, Long employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        
        if (employeeOpt.isEmpty() || eventOpt.isEmpty()) {
            return false;
        }
        
        Employee employee = employeeOpt.get();
        Event event = eventOpt.get();
        
        // 檢查權限是否已存在
        if (eventViewPermissionRepository.existsByEmployeeAndEvent(employee, event)) {
            return true;  // 已有權限，視為添加成功
        }
        
        // 創建新權限
        EventViewPermission permission = new EventViewPermission();
        permission.setEvent(event);
        permission.setEmployee(employee);
        permission.setGrantedAt(LocalDateTime.now());
        
        eventViewPermissionRepository.save(permission);
        
        return true;
    }
    
    /**
     * 移除事件查看權限
     * 
     * @param eventId 事件ID
     * @param employeeId 員工ID
     * @return 是否成功移除
     */
    @Transactional
    public boolean removeViewPermission(Long eventId, Long employeeId) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        
        if (employeeOpt.isEmpty() || eventOpt.isEmpty()) {
            return false;
        }
        
        Employee employee = employeeOpt.get();
        Event event = eventOpt.get();
        
        // 檢查權限是否存在
        Optional<EventViewPermission> permissionOpt = 
            eventViewPermissionRepository.findByEventAndEmployee(event, employee);
        
        if (permissionOpt.isEmpty()) {
            return true;  // 已無權限，視為移除成功
        }
        
        // 刪除權限
        eventViewPermissionRepository.delete(permissionOpt.get());
        
        return true;
    }
    
    /**
     * 獲取有特殊查看權限的員工ID列表
     * 
     * @param eventId 事件ID
     * @return 有特殊查看權限的員工ID集合
     */
    public Set<Long> getSpecialViewerIds(Long eventId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        
        if (eventOpt.isEmpty()) {
            return new HashSet<>();
        }
        
        Event event = eventOpt.get();
        
        // 獲取所有權限記錄
        List<EventViewPermission> permissions = eventViewPermissionRepository.findByEvent(event);
        
        // 提取員工ID
        return permissions.stream()
                .map(permission -> permission.getEmployee().getId())
                .collect(Collectors.toSet());
    }
}