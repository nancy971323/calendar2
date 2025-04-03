package com.company.calendar.controller;

import com.company.calendar.enums.SecurityLevel;
import com.company.calendar.model.Employee;
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
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }
    
    /**
     * 獲取所有活躍員工
     * 
     * @return 所有活躍員工的列表
     */
    @GetMapping("/active")
    public ResponseEntity<List<Employee>> getAllActiveEmployees() {
        List<Employee> employees = employeeService.getAllActiveEmployees();
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
        Employee employee = employeeService.getEmployeeById(id);
        
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
        Employee employee = employeeService.getEmployeeByUsername(username);
        
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("員工不存在");
        }
        
        return ResponseEntity.ok(employee);
    }
    
    /**
     * 創建新員工
     * 
     * @param employee 員工信息
     * @return 創建的員工信息
     */
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody Employee employee) {
        try {
            Employee createdEmployee = employeeService.createEmployee(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 更新員工信息
     * 
     * @param id 員工ID
     * @param employee 員工信息
     * @return 更新後的員工信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        if (!id.equals(employee.getId())) {
            return ResponseEntity.badRequest().body("ID不匹配");
        }
        
        try {
            Employee updatedEmployee = employeeService.updateEmployee(employee);
            return ResponseEntity.ok(updatedEmployee);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 修改員工密碼
     * 
     * @param id 員工ID
     * @param passwordMap 新舊密碼
     * @return 操作結果
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordMap) {
        String oldPassword = passwordMap.get("oldPassword");
        String newPassword = passwordMap.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body("缺少必要參數");
        }
        
        try {
            boolean success = employeeService.changePassword(id, oldPassword, newPassword);
            
            if (success) {
                return ResponseEntity.ok().body("密碼修改成功");
            } else {
                return ResponseEntity.badRequest().body("原密碼錯誤");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    /**
     * 啟用或禁用員工
     * 
     * @param id 員工ID
     * @param activeMap 狀態信息
     * @return 操作結果
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> setEmployeeStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> activeMap) {
        Boolean active = activeMap.get("active");
        
        if (active == null) {
            return ResponseEntity.badRequest().body("缺少必要參數");
        }
        
        Employee employee = employeeService.setEmployeeStatus(id, active);
        
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("員工不存在");
        }
        
        return ResponseEntity.ok(employee);
    }
    
    /**
     * 刪除員工
     * 
     * @param id 員工ID
     * @return 操作結果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        boolean deleted = employeeService.deleteEmployee(id);
        
        if (deleted) {
            return ResponseEntity.ok().body("員工已刪除");
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
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable String department) {
        List<Employee> employees = employeeService.getEmployeesByDepartment(department);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * 根據安全等級獲取員工
     * 
     * @param level 安全等級
     * @return 指定安全等級的員工列表
     */
    @GetMapping("/security-level/{level}")
    public ResponseEntity<List<Employee>> getEmployeesBySecurityLevel(@PathVariable SecurityLevel level) {
        List<Employee> employees = employeeService.getEmployeesBySecurityLevel(level);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * 獲取低於指定安全等級的員工
     * 
     * @param level 安全等級
     * @return 低於指定安全等級的員工列表
     */
    @GetMapping("/security-level/lower-than/{level}")
    public ResponseEntity<List<Employee>> getEmployeesWithLowerSecurityLevel(@PathVariable SecurityLevel level) {
        List<Employee> employees = employeeService.getEmployeesWithLowerSecurityLevel(level);
        return ResponseEntity.ok(employees);
    }
    
    /**
     * 獲取所有安全等級
     * 
     * @return 所有安全等級列表
     */
    @GetMapping("/security-levels")
    public ResponseEntity<Map<String, Object>> getAllSecurityLevels() {
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