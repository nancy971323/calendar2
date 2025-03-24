package com.company.calendar.service.calendar;

import com.company.calendar.dto.EventDTO;
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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 行事曆服務實現類
 * <p>
 * 實現CalendarService介面，提供事件相關業務邏輯的具體實現。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Service
public class CalendarServiceImpl implements CalendarService {

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
    public CalendarServiceImpl(EventRepository eventRepository,
                              EmployeeRepository employeeRepository,
                              EventViewPermissionRepository eventViewPermissionRepository) {
        this.eventRepository = eventRepository;
        this.employeeRepository = employeeRepository;
        this.eventViewPermissionRepository = eventViewPermissionRepository;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                            .map(EventDTO::new)
                            .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EventDTO getEventById(Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        return eventOpt.map(EventDTO::new).orElse(null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EventDTO createEvent(EventDTO eventDTO, Long creatorId) {
        Optional<Employee> creatorOpt = employeeRepository.findById(creatorId);
        if (creatorOpt.isEmpty()) {
            throw new IllegalArgumentException("創建者不存在: " + creatorId);
        }
        
        Employee creator = creatorOpt.get();
        
        Event event = eventDTO.toEntity(creator);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        
        // 保存事件
        Event savedEvent = eventRepository.save(event);
        
        // 添加特殊查看權限
        if (eventDTO.getSpecialViewerIds() != null && !eventDTO.getSpecialViewerIds().isEmpty()) {
            for (Long viewerId : eventDTO.getSpecialViewerIds()) {
                Optional<Employee> viewerOpt = employeeRepository.findById(viewerId);
                if (viewerOpt.isPresent()) {
                    savedEvent.addViewPermission(viewerOpt.get());
                }
            }
            savedEvent = eventRepository.save(savedEvent);
        }
        
        return new EventDTO(savedEvent);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EventDTO updateEvent(Long id, EventDTO eventDTO) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            throw new IllegalArgumentException("事件不存在: " + id);
        }
        
        Event event = eventOpt.get();
        
        // 更新事件基本信息
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setStartTime(eventDTO.getStartTime());
        event.setEndTime(eventDTO.getEndTime());
        event.setLocation(eventDTO.getLocation());
        event.setSecurityLevel(eventDTO.getSecurityLevel());
        event.setUpdatedAt(LocalDateTime.now());
        
        // 保存事件
        Event updatedEvent = eventRepository.save(event);
        
        // 更新特殊查看權限
        if (eventDTO.getSpecialViewerIds() != null) {
            // 獲取當前的特殊查看權限
            Set<Long> currentViewerIds = event.getViewPermissions().stream()
                                        .map(permission -> permission.getEmployee().getId())
                                        .collect(Collectors.toSet());
            
            // 添加新的查看權限
            for (Long viewerId : eventDTO.getSpecialViewerIds()) {
                if (!currentViewerIds.contains(viewerId)) {
                    Optional<Employee> viewerOpt = employeeRepository.findById(viewerId);
                    if (viewerOpt.isPresent()) {
                        updatedEvent.addViewPermission(viewerOpt.get());
                    }
                }
            }
            
            // 移除不再需要的查看權限
            for (Long viewerId : currentViewerIds) {
                if (!eventDTO.getSpecialViewerIds().contains(viewerId)) {
                    Optional<Employee> viewerOpt = employeeRepository.findById(viewerId);
                    if (viewerOpt.isPresent()) {
                        updatedEvent.removeViewPermission(viewerOpt.get());
                    }
                }
            }
            
            updatedEvent = eventRepository.save(updatedEvent);
        }
        
        return new EventDTO(updatedEvent);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean deleteEvent(Long id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            return false;
        }
        
        Event event = eventOpt.get();
        
        // 刪除事件的查看權限
        eventViewPermissionRepository.deleteByEvent(event);
        
        // 刪除事件
        eventRepository.delete(event);
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventDTO> getEventsBetween(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByStartTimeBetween(start, end).stream()
                            .map(EventDTO::new)
                            .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventDTO> getEventsByYearAndMonth(int year, int month) {
        return eventRepository.findByYearAndMonth(year, month).stream()
                            .map(EventDTO::new)
                            .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventDTO> getVisibleEventsForEmployee(Long employeeId, LocalDateTime start, LocalDateTime end) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("員工不存在: " + employeeId);
        }
        
        Employee employee = employeeOpt.get();
        
        return eventRepository.findVisibleEventsForEmployee(employee, employee.getSecurityLevel(), start, end).stream()
                            .map(EventDTO::new)
                            .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventDTO> getEventsByCreator(Long creatorId) {
        Optional<Employee> creatorOpt = employeeRepository.findById(creatorId);
        if (creatorOpt.isEmpty()) {
            throw new IllegalArgumentException("創建者不存在: " + creatorId);
        }
        
        Employee creator = creatorOpt.get();
        
        return eventRepository.findByCreator(creator).stream()
                            .map(EventDTO::new)
                            .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventDTO> searchEvents(String keyword) {
        return eventRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword).stream()
                            .map(EventDTO::new)
                            .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean addViewPermission(Long eventId, Long employeeId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        
        if (eventOpt.isEmpty() || employeeOpt.isEmpty()) {
            return false;
        }
        
        Event event = eventOpt.get();
        Employee employee = employeeOpt.get();
        
        // 檢查是否已存在權限
        if (eventViewPermissionRepository.existsByEmployeeAndEvent(employee, event)) {
            return true; // 已經有權限，視為添加成功
        }
        
        // 添加查看權限
        event.addViewPermission(employee);
        eventRepository.save(event);
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean removeViewPermission(Long eventId, Long employeeId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        
        if (eventOpt.isEmpty() || employeeOpt.isEmpty()) {
            return false;
        }
        
        Event event = eventOpt.get();
        Employee employee = employeeOpt.get();
        
        // 檢查是否存在權限
        Optional<EventViewPermission> permissionOpt = eventViewPermissionRepository.findByEmployeeAndEvent(employee, event);
        if (permissionOpt.isEmpty()) {
            return true; // 已經沒有權限，視為移除成功
        }
        
        // 移除查看權限
        EventViewPermission permission = permissionOpt.get();
        eventViewPermissionRepository.delete(permission);
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Long> getSpecialViewerIds(Long eventId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return new HashSet<>();
        }
        
        Event event = eventOpt.get();
        
        return eventViewPermissionRepository.findByEvent(event).stream()
                                         .map(permission -> permission.getEmployee().getId())
                                         .collect(Collectors.toSet());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canEmployeeViewEvent(Long eventId, Long employeeId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        
        if (eventOpt.isEmpty() || employeeOpt.isEmpty()) {
            return false;
        }
        
        Event event = eventOpt.get();
        Employee employee = employeeOpt.get();
        
        return event.canBeViewedBy(employee);
    }
}