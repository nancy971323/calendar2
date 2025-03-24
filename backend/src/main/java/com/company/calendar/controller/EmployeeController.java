package com.company.calendar.controller;

import com.company.calendar.dto.EmployeeDTO;
import com.company.calendar.enums.SecurityLevel;
import com.company.calendar.service.employee.EmployeeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 員工控制器
 * <p>
 * 處理員工相關的HTTP請求，包括員工的CRUD操作和其他業務功能。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    /**
     * 員工服務
     */
    private final EmployeeService employeeService;
    
    /**
     * 構造函數
     * 
     * @param employeeService 員工服務
     */
    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    
    /**
     * 獲取所有員工
     * 
     * @return 所有員工的列表
     */
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * 獲取所有活躍員工
     * 
     * @return 所有活躍員工的列表
     */
    @GetMapping("/active")
    public ResponseEntity<List<EmployeeDTO>> getAllActiveEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllActiveEmployees();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * 根據ID獲取員工
     * 
     * @param id 員工ID
     * @return 員工信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("員工不存在");
        }
        
        return ResponseEntity.ok(employee);
    }
    
    /**
     * 根據用戶名獲取員工
     * 
     * @param username 用戶名
     * @return 員工信息
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getEmployeeByUsername(@PathVariable String username) {
        EmployeeDTO employee = employeeService.getEmployeeByUsername(username);
        
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("員工不存在");
        }
        
        return ResponseEntity.ok(employee);
    }
    
    /**
     * 創建新員工
     * 
     * @param request 包含員工信息和密碼的請求
     * @return 創建的員工信息
     */
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody Map<String, Object> request) {
        try {
            EmployeeDTO employeeDTO = new EmployeeDTO();
            
            employeeDTO.setUsername((String) request.get("username"));
            employeeDTO.setFullName((String) request.get("fullName"));
            employeeDTO.setEmail((String) request.get("email"));
            employeeDTO.setDepartment((String) request.get("department"));
            
            String securityLevelStr = (String) request.get("securityLevel");
            SecurityLevel securityLevel = SecurityLevel.valueOf(securityLevelStr);
            employeeDTO.setSecurityLevel(securityLevel);
            
            String password = (String) request.get("password");
            
            EmployeeDTO createdEmployee = employeeService.createEmployee(employeeDTO, password);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 更新員工信息
     * 
     * @param id 員工ID
     * @param employeeDTO 員工資料傳輸物件
     * @return 更新後的員工信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        try {
            EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
            return ResponseEntity.ok(updatedEmployee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 更新員工密碼
     * 
     * @param id 員工ID
     * @param request 包含新密碼的請求
     * @return 更新結果
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String newPassword = request.get("password");
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("密碼不能為空");
        }
        
        boolean success = employeeService.updatePassword(id, newPassword);
        
        if (success) {
            return ResponseEntity.ok().body("密碼更新成功");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("員工不存在");
        }
    }
    
    /**
     * 刪除員工
     * 
     * @param id 員工ID
     * @return 刪除結果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        boolean success = employeeService.deleteEmployee(id);
        
        if (success) {
            return ResponseEntity.ok().body("員工刪除成功");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("員工不存在");
        }
    }
    
    /**
     * 根據部門獲取員工
     * 
     * @param department 部門
     * @return 指定部門的員工列表
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<EmployeeDTO>> getEmployeesByDepartment(@PathVariable String department) {
        List<EmployeeDTO> employees = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * 根據安全等級獲取員工
     * 
     * @param level 安全等級
     * @return 指定安全等級的員工列表
     */
    @GetMapping("/security-level/{level}")
    public ResponseEntity<?> getEmployeesBySecurityLevel(@PathVariable String level) {
        try {
            SecurityLevel securityLevel = SecurityLevel.valueOf(level);
            List<EmployeeDTO> employees = employeeService.getEmployeesBySecurityLevel(securityLevel);
            return ResponseEntity.ok(employees);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("無效的安全等級: " + level);
        }
    }
    
    /**
     * 獲取安全等級低於指定等級的員工
     * 
     * @param level 安全等級
     * @return 安全等級低於指定等級的員工列表
     */
    @GetMapping("/lower-security-level/{level}")
    public ResponseEntity<?> getEmployeesWithLowerSecurityLevel(@PathVariable String level) {
        try {
            SecurityLevel securityLevel = SecurityLevel.valueOf(level);
            List<EmployeeDTO> employees = employeeService.getEmployeesWithLowerSecurityLevel(securityLevel);
            return ResponseEntity.ok(employees);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("無效的安全等級: " + level);
        }
    }
    
    /**
     * 獲取所有安全等級枚舉值
     * 
     * @return 安全等級枚舉值列表
     */
    @GetMapping("/security-levels")
    public ResponseEntity<Map<String, Object>> getSecurityLevels() {
        Map<String, Object> response = new HashMap<>();
        
        for (SecurityLevel level : SecurityLevel.values()) {
            Map<String, Object> levelInfo = new HashMap<>();
            levelInfo.put("level", level.getLevel());
            levelInfo.put("description", level.getDescription());
            
            response.put(level.name(), levelInfo);
        }
        
        return ResponseEntity.ok(response);
    }
}