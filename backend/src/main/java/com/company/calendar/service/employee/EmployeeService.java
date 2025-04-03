package com.company.calendar.service.employee;

import com.company.calendar.enums.SecurityLevel;
import com.company.calendar.model.Employee;
import com.company.calendar.repository.EmployeeRepository;
import com.company.calendar.repository.EventViewPermissionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 員工服務類
 * <p>
 * 提供員工相關業務邏輯的具體實現，包括員工的CRUD操作和其他業務功能。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Service
public class EmployeeService {

    /**
     * 員工資料庫操作介面
     */
    private final EmployeeRepository employeeRepository;
    
    /**
     * 事件查看權限資料庫操作介面
     */
    private final EventViewPermissionRepository eventViewPermissionRepository;
    
    /**
     * 密碼編碼器
     */
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 構造函數
     * 
     * @param employeeRepository 員工資料庫操作介面
     * @param eventViewPermissionRepository 事件查看權限資料庫操作介面
     * @param passwordEncoder 密碼編碼器
     */
    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           EventViewPermissionRepository eventViewPermissionRepository,
                           PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.eventViewPermissionRepository = eventViewPermissionRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * 獲取所有員工
     * 
     * @return 所有員工的列表
     */
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    /**
     * 根據ID獲取員工
     * 
     * @param id 員工ID
     * @return 員工信息，如果不存在則返回null
     */
    public Employee getEmployeeById(Long id) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        return employeeOpt.orElse(null);
    }
    
    /**
     * 根據用戶名獲取員工
     * 
     * @param username 用戶名
     * @return 員工信息，如果不存在則返回null
     */
    public Employee getEmployeeByUsername(String username) {
        Optional<Employee> employeeOpt = employeeRepository.findByUsername(username);
        return employeeOpt.orElse(null);
    }
    
    /**
     * 創建新員工
     * 
     * @param employee 員工信息（包含密碼）
     * @return 創建的員工信息
     * @throws IllegalArgumentException 如果用戶名或郵箱已存在
     */
    @Transactional
    public Employee createEmployee(Employee employee) throws IllegalArgumentException {
        // 檢查用戶名和郵箱是否已存在
        if (employeeRepository.existsByUsername(employee.getUsername())) {
            throw new IllegalArgumentException("用戶名已存在: " + employee.getUsername());
        }
        
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("電子郵件已存在: " + employee.getEmail());
        }
        
        // 加密密碼
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        
        // 設置時間和狀態
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);
        employee.setActive(true);
        
        // 保存員工
        return employeeRepository.save(employee);
    }
    
    /**
     * 更新員工信息
     * 
     * @param employee 員工信息
     * @return 更新後的員工信息
     * @throws IllegalArgumentException 如果用戶名或郵箱已被其他員工使用
     */
    @Transactional
    public Employee updateEmployee(Employee employee) throws IllegalArgumentException {
        // 檢查員工是否存在
        Optional<Employee> existingEmployeeOpt = employeeRepository.findById(employee.getId());
        if (existingEmployeeOpt.isEmpty()) {
            throw new IllegalArgumentException("員工不存在: " + employee.getId());
        }
        
        Employee existingEmployee = existingEmployeeOpt.get();
        
        // 檢查用戶名是否已被其他員工使用
        if (!existingEmployee.getUsername().equals(employee.getUsername()) &&
            employeeRepository.existsByUsername(employee.getUsername())) {
            throw new IllegalArgumentException("用戶名已存在: " + employee.getUsername());
        }
        
        // 檢查郵箱是否已被其他員工使用
        if (!existingEmployee.getEmail().equals(employee.getEmail()) &&
            employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("電子郵件已存在: " + employee.getEmail());
        }
        
        // 保留原密碼
        employee.setPassword(existingEmployee.getPassword());
        
        // 更新時間
        employee.setUpdatedAt(LocalDateTime.now());
        
        // 保存更新
        return employeeRepository.save(employee);
    }
    
    /**
     * 修改員工密碼
     * 
     * @param id 員工ID
     * @param oldPassword 舊密碼
     * @param newPassword 新密碼
     * @return 是否成功修改
     * @throws IllegalArgumentException 如果員工不存在
     */
    @Transactional
    public boolean changePassword(Long id, String oldPassword, String newPassword) throws IllegalArgumentException {
        // 檢查員工是否存在
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("員工不存在: " + id);
        }
        
        Employee employee = employeeOpt.get();
        
        // 檢查原密碼是否正確
        if (!passwordEncoder.matches(oldPassword, employee.getPassword())) {
            return false;
        }
        
        // 更新密碼
        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setUpdatedAt(LocalDateTime.now());
        employeeRepository.save(employee);
        
        return true;
    }
    
    /**
     * 啟用或禁用員工
     * 
     * @param id 員工ID
     * @param active 是否啟用
     * @return 更新後的員工信息，如果不存在則返回null
     */
    @Transactional
    public Employee setEmployeeStatus(Long id, boolean active) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            return null;
        }
        
        Employee employee = employeeOpt.get();
        employee.setActive(active);
        employee.setUpdatedAt(LocalDateTime.now());
        
        return employeeRepository.save(employee);
    }
    
    /**
     * 刪除員工
     * 
     * @param id 員工ID
     * @return 是否成功刪除
     */
    @Transactional
    public boolean deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            return false;
        }
        
        // 刪除相關的事件查看權限
        eventViewPermissionRepository.deleteByEmployee(employeeRepository.getReferenceById(id));
        
        // 刪除員工
        employeeRepository.deleteById(id);
        
        return true;
    }
    
    /**
     * 獲取所有活躍員工
     * 
     * @return 所有活躍員工的列表
     */
    public List<Employee> getAllActiveEmployees() {
        return employeeRepository.findByActiveTrue();
    }
    
    /**
     * 根據部門獲取員工
     * 
     * @param department 部門
     * @return 指定部門的員工列表
     */
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }
    
    /**
     * 根據安全等級獲取員工
     * 
     * @param securityLevel 安全等級
     * @return 指定安全等級的員工列表
     */
    public List<Employee> getEmployeesBySecurityLevel(SecurityLevel securityLevel) {
        return employeeRepository.findBySecurityLevel(securityLevel);
    }
    
    /**
     * 獲取安全等級低於指定等級的員工
     * 
     * @param securityLevel 安全等級
     * @return 安全等級低於指定等級的員工列表
     */
    public List<Employee> getEmployeesWithLowerSecurityLevel(SecurityLevel securityLevel) {
        return employeeRepository.findBySecurityLevelGreaterThan(securityLevel);
    }
}