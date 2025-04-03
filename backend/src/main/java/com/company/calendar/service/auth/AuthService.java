package com.company.calendar.service.auth;

import com.company.calendar.model.Employee;
import com.company.calendar.repository.EmployeeRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

/**
 * 認證服務類
 * <p>
 * 提供認證相關業務邏輯的具體實現，包括登入、登出和用戶信息獲取等功能。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Service
public class AuthService {

    /**
     * 員工資料庫操作介面
     */
    private final EmployeeRepository employeeRepository;
    
    /**
     * 密碼編碼器
     */
    private final PasswordEncoder passwordEncoder;
    
    /**
     * JWT密鑰
     */
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    /**
     * JWT令牌有效期（毫秒）
     */
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;
    
    /**
     * 構造函數
     * 
     * @param employeeRepository 員工資料庫操作介面
     * @param passwordEncoder 密碼編碼器
     */
    @Autowired
    public AuthService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * 處理用戶登入
     * 
     * @param username a用戶名
     * @param password 密碼
     * @return 登入成功返回員工信息，否則返回null
     */
    public Employee login(String username, String password) {
        Optional<Employee> employeeOpt = employeeRepository.findByUsername(username);
        
        if (employeeOpt.isEmpty() || !passwordEncoder.matches(password, employeeOpt.get().getPassword())) {
            return null;
        }
        
        Employee employee = employeeOpt.get();
        
        // 設置認證信息
        UserDetails userDetails = User.builder()
                                    .username(employee.getUsername())
                                    .password(employee.getPassword())
                                    .authorities(Collections.singletonList(
                                        new SimpleGrantedAuthority("ROLE_" + employee.getSecurityLevel().name())
                                    ))
                                    .build();
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        return employee;
    }
    
    /**
     * 獲取當前登入的員工信息
     * 
     * @return 當前員工信息，如果未登入則返回null
     */
    public Employee getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<Employee> employeeOpt = employeeRepository.findByUsername(username);
        return employeeOpt.orElse(null);
    }
    
    /**
     * 檢查當前用戶是否已認證
     * 
     * @return 是否已認證
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
               !"anonymousUser".equals(authentication.getPrincipal());
    }
    
    /**
     * 登出當前用戶
     */
    public void logout() {
        SecurityContextHolder.clearContext();
    }
    
    /**
     * 創建JWT令牌
     * 
     * @param username 用戶名
     * @return JWT令牌
     */
    public String createToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                   .setSubject(username)
                   .setIssuedAt(now)
                   .setExpiration(expiryDate)
                   .signWith(key, SignatureAlgorithm.HS256)
                   .compact();
    }
    
    /**
     * 驗證JWT令牌
     * 
     * @param token JWT令牌
     * @return 令牌包含的用戶名，如果令牌無效則返回null
     */
    public String validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                               .setSigningKey(key)
                               .build()
                               .parseClaimsJws(token)
                               .getBody();
            
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}