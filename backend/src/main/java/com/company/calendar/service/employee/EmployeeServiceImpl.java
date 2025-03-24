package com.company.calendar.service.employee;

import com.company.calendar.dto.EmployeeDTO;
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
import java.util.stream.Collectors;

/**
 * 員工服務實現類
 * <p>
 * 實現EmployeeService介面，提供員工相關業務邏輯的具體實現。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

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
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               EventViewPermissionRepository eventViewPermissionRepository,
                               PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.eventViewPermissionRepository = eventViewPermissionRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                                .map(EmployeeDTO::new)
                                .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        return employeeOpt.map(EmployeeDTO::new).orElse(null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EmployeeDTO getEmployeeByUsername(String username) {
        Optional<Employee> employeeOpt = employeeRepository.findByUsername(username);
        return employeeOpt.map(EmployeeDTO::new).orElse(null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO, String rawPassword) {
        // 檢查用戶名和電子郵件是否已存在
        if (employeeRepository.existsByUsername(employeeDTO.getUsername())) {
            throw new IllegalArgumentException("用戶名已存在: " + employeeDTO.getUsername());
        }
        
        if (employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new IllegalArgumentException("電子郵件已存在: " + employeeDTO.getEmail());
        }
        
        // 創建新員工
        Employee employee = employeeDTO.toEntity();
        employee.setPassword(passwordEncoder.encode(rawPassword));
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        employee.setActive(true);
        
        Employee savedEmployee = employeeRepository.save(employee);
        return new EmployeeDTO(savedEmployee);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO employeeDTO) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("員工不存在: " + id);
        }
        
        Employee employee = employeeOpt.get();
        
        // 檢查用戶名和電子郵件是否與其他員工重複
        if (!employee.getUsername().equals(employeeDTO.getUsername()) &&
            employeeRepository.existsByUsername(employeeDTO.getUsername())) {
            throw new IllegalArgumentException("用戶名已存在: " + employeeDTO.getUsername());
        }
        
        if (!employee.getEmail().equals(employeeDTO.getEmail()) &&
            employeeRepository.existsByEmail(employeeDTO.getEmail())) {
            throw new IllegalArgumentException("電子郵件已存在: " + employeeDTO.getEmail());
        }
        
        // 更新員工信息
        employee.setUsername(employeeDTO.getUsername());
        employee.setFullName(employeeDTO.getFullName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setDepartment(employeeDTO.getDepartment());
        employee.setSecurityLevel(employeeDTO.getSecurityLevel());
        employee.setActive(employeeDTO.isActive());
        employee.setUpdatedAt(LocalDateTime.now());
        
        Employee updatedEmployee = employeeRepository.save(employee);
        return new EmployeeDTO(updatedEmployee);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean updatePassword(Long id, String newPassword) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            return false;
        }
        
        Employee employee = employeeOpt.get();
        employee.setPassword(passwordEncoder.encode(newPassword));
        employee.setUpdatedAt(LocalDateTime.now());
        
        employeeRepository.save(employee);
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public boolean deleteEmployee(Long id) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            return false;
        }
        
        Employee employee = employeeOpt.get();
        
        // 刪除員工的事件查看權限
        eventViewPermissionRepository.deleteByEmployee(employee);
        
        // 刪除員工
        employeeRepository.delete(employee);
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmployeeDTO> getAllActiveEmployees() {
        return employeeRepository.findByActiveTrue().stream()
                                .map(EmployeeDTO::new)
                                .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmployeeDTO> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department).stream()
                                .map(EmployeeDTO::new)
                                .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmployeeDTO> getEmployeesBySecurityLevel(SecurityLevel securityLevel) {
        return employeeRepository.findBySecurityLevel(securityLevel).stream()
                                .map(EmployeeDTO::new)
                                .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmployeeDTO> getEmployeesWithLowerSecurityLevel(SecurityLevel securityLevel) {
        return employeeRepository.findBySecurityLevelGreaterThan(securityLevel).stream()
                                .map(EmployeeDTO::new)
                                .collect(Collectors.toList());
    }
}