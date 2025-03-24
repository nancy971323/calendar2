package com.company.calendar.service.employee;

import com.company.calendar.dto.EmployeeDTO;
import com.company.calendar.enums.SecurityLevel;

import java.util.List;

/**
 * 員工服務介面
 * <p>
 * 定義處理員工相關業務邏輯的服務介面，包括員工的CRUD操作和其他業務功能。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
public interface EmployeeService {
    
    /**
     * 獲取所有員工
     * 
     * @return 所有員工的列表
     */
    List<EmployeeDTO> getAllEmployees();
    
    /**
     * 根據ID獲取員工
     * 
     * @param id 員工ID
     * @return 員工資料傳輸物件，如果未找到則返回null
     */
    EmployeeDTO getEmployeeById(Long id);
    
    /**
     * 根據用戶名獲取員工
     * 
     * @param username 用戶名
     * @return 員工資料傳輸物件，如果未找到則返回null
     */
    EmployeeDTO getEmployeeByUsername(String username);
    
    /**
     * 創建新員工
     * 
     * @param employeeDTO 員工資料傳輸物件
     * @param rawPassword 原始密碼
     * @return 創建的員工資料傳輸物件
     */
    EmployeeDTO createEmployee(EmployeeDTO employeeDTO, String rawPassword);
    
    /**
     * 更新員工信息
     * 
     * @param id 員工ID
     * @param employeeDTO 員工資料傳輸物件
     * @return 更新後的員工資料傳輸物件
     */
    EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO);
    
    /**
     * 更新員工密碼
     * 
     * @param id 員工ID
     * @param newPassword 新密碼
     * @return 是否更新成功
     */
    boolean updatePassword(Long id, String newPassword);
    
    /**
     * 刪除員工
     * 
     * @param id 員工ID
     * @return 是否刪除成功
     */
    boolean deleteEmployee(Long id);
    
    /**
     * 獲取所有活躍的員工
     * 
     * @return 所有活躍員工的列表
     */
    List<EmployeeDTO> getAllActiveEmployees();
    
    /**
     * 根據部門獲取員工
     * 
     * @param department 部門
     * @return 指定部門的員工列表
     */
    List<EmployeeDTO> getEmployeesByDepartment(String department);
    
    /**
     * 根據安全等級獲取員工
     * 
     * @param securityLevel 安全等級
     * @return 指定安全等級的員工列表
     */
    List<EmployeeDTO> getEmployeesBySecurityLevel(SecurityLevel securityLevel);
    
    /**
     * 獲取安全等級低於指定等級的員工
     * 
     * @param securityLevel 安全等級
     * @return 安全等級低於指定等級的員工列表
     */
    List<EmployeeDTO> getEmployeesWithLowerSecurityLevel(SecurityLevel securityLevel);
}