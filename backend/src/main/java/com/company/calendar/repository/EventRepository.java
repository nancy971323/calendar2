package com.company.calendar.repository;

import com.company.calendar.enums.SecurityLevel;
import com.company.calendar.model.Employee;
import com.company.calendar.model.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 事件資料庫操作介面
 * <p>
 * 提供對事件實體在資料庫中的基本CRUD操作以及自定義查詢方法。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    /**
     * 查找指定創建者創建的所有事件
     * 
     * @param creator 創建者
     * @return 創建者創建的事件列表
     */
    List<Event> findByCreator(Employee creator);
    
    /**
     * 查找指定安全等級的所有事件
     * 
     * @param securityLevel 安全等級
     * @return 指定安全等級的事件列表
     */
    List<Event> findBySecurityLevel(SecurityLevel securityLevel);
    
    /**
     * 查找指定時間區間內的所有事件
     * 
     * @param start 開始時間
     * @param end 結束時間
     * @return 時間區間內的事件列表
     */
    List<Event> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 查找指定地點的所有事件
     * 
     * @param location 地點
     * @return 指定地點的事件列表
     */
    List<Event> findByLocationContaining(String location);
    
    /**
     * 查找指定員工有權限查看的所有事件，包括：
     * 1. 員工創建的事件
     * 2. 安全等級低於或等於員工安全等級的事件
     * 3. 員工被特別授權查看的事件
     * 
     * @param employee 指定員工
     * @param employeeLevel 員工安全等級
     * @param start 開始時間
     * @param end 結束時間
     * @return 員工有權限查看的事件列表
     */
    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN e.viewPermissions p " +
           "WHERE (e.creator = :employee OR e.securityLevel >= :employeeLevel OR p.employee = :employee) " +
           "AND (e.startTime BETWEEN :start AND :end OR e.endTime BETWEEN :start AND :end)")
    List<Event> findVisibleEventsForEmployee(
        @Param("employee") Employee employee,
        @Param("employeeLevel") SecurityLevel employeeLevel,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    /**
     * 查找指定月份的所有事件
     * 
     * @param year 年份
     * @param month 月份（1-12）
     * @return 指定月份的事件列表
     */
    @Query("SELECT e FROM Event e WHERE EXTRACT(YEAR FROM e.startTime) = :year AND EXTRACT(MONTH FROM e.startTime) = :month")
    List<Event> findByYearAndMonth(@Param("year") int year, @Param("month") int month);
    
    /**
     * 搜索事件標題或描述包含指定關鍵字的所有事件
     * 
     * @param keyword 關鍵字
     * @return 匹配的事件列表
     */
    List<Event> findByTitleContainingOrDescriptionContaining(String keyword, String sameKeyword);
}