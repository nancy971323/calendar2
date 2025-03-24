package com.company.calendar.repository;

import com.company.calendar.enums.SecurityLevel;
import com.company.calendar.model.Employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 員工資料庫操作介面
 * <p>
 * 提供對員工實體在資料庫中的基本CRUD操作以及自定義查詢方法。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    /**
     * 根據用戶名查找員工
     * 
     * @param username 用戶名
     * @return 查詢結果，如果未找到則返回空
     */
    Optional<Employee> findByUsername(String username);
    
    /**
     * 根據電子郵件查找員工
     * 
     * @param email 電子郵件
     * @return 查詢結果，如果未找到則返回空
     */
    Optional<Employee> findByEmail(String email);
    
    /**
     * 檢查用戶名是否已存在
     * 
     * @param username 用戶名
     * @return 如果存在返回true，否則返回false
     */
    boolean existsByUsername(String username);
    
    /**
     * 檢查電子郵件是否已存在
     * 
     * @param email 電子郵件
     * @return 如果存在返回true，否則返回false
     */
    boolean existsByEmail(String email);
    
    /**
     * 查找所有活躍的員工
     * 
     * @return 活躍員工列表
     */
    List<Employee> findByActiveTrue();
    
    /**
     * 根據部門查找員工
     * 
     * @param department 部門
     * @return 指定部門的員工列表
     */
    List<Employee> findByDepartment(String department);
    
    /**
     * 根據安全等級查找員工
     * 
     * @param securityLevel 安全等級
     * @return 指定安全等級的員工列表
     */
    List<Employee> findBySecurityLevel(SecurityLevel securityLevel);
    
    /**
     * 查找安全等級低於指定等級的員工
     * 
     * @param securityLevel 安全等級
     * @return 安全等級低於指定等級的員工列表
     */
    List<Employee> findBySecurityLevelGreaterThan(SecurityLevel securityLevel);
}