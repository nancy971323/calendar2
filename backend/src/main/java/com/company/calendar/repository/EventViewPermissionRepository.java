package com.company.calendar.repository;

import com.company.calendar.model.Employee;
import com.company.calendar.model.Event;
import com.company.calendar.model.EventViewPermission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 事件查看權限資料庫操作介面
 * <p>
 * 提供對事件查看權限實體在資料庫中的基本CRUD操作以及自定義查詢方法。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Repository
public interface EventViewPermissionRepository extends JpaRepository<EventViewPermission, Long> {
    
    /**
     * 查找指定員工的所有查看權限
     * 
     * @param employee 員工
     * @return 員工的查看權限列表
     */
    List<EventViewPermission> findByEmployee(Employee employee);
    
    /**
     * 查找指定事件的所有查看權限
     * 
     * @param event 事件
     * @return 事件的查看權限列表
     */
    List<EventViewPermission> findByEvent(Event event);
    
    /**
     * 查找指定員工對指定事件的查看權限
     * 
     * @param employee 員工
     * @param event 事件
     * @return 查看權限，如果不存在則返回空
     */
    Optional<EventViewPermission> findByEmployeeAndEvent(Employee employee, Event event);
    
    /**
     * 刪除指定員工的所有查看權限
     * 
     * @param employee 員工
     * @return 刪除的記錄數
     */
    long deleteByEmployee(Employee employee);
    
    /**
     * 刪除指定事件的所有查看權限
     * 
     * @param event 事件
     * @return 刪除的記錄數
     */
    long deleteByEvent(Event event);
    
    /**
     * 檢查指定員工是否有權限查看指定事件
     * 
     * @param employee 員工
     * @param event 事件
     * @return 如果有權限則返回true，否則返回false
     */
    boolean existsByEmployeeAndEvent(Employee employee, Event event);
}