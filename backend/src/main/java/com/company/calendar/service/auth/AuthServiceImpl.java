package com.company.calendar.service.auth;

import com.company.calendar.dto.EmployeeDTO;
import com.company.calendar.dto.LoginDTO;
import com.company.calendar.model.Employee;
import com.company.calendar.repository.EmployeeRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

/**
 * 認證服務實現類
 * <p>
 * 實現AuthService介面，提供用戶認證相關功能的具體實現。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Service
public class AuthServiceImpl implements AuthService {

    /**
     * JWT密鑰
     */
    @Value("${jwt.secret:defaultSecretKeyNeedsToBeAtLeast32BytesLong}")
    private String jwtSecret;
    
    /**
     * JWT令牌有效期（毫秒）
     */
    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;
    
    /**
     * 認證管理器
     */
    private final AuthenticationManager authenticationManager;
    
    /**
     * 密碼編碼器
     */
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 員工資料庫操作介面
     */
    private final EmployeeRepository employeeRepository;
    
    /**
     * 構造函數
     * 
     * @param authenticationManager 認證管理器
     * @param passwordEncoder 密碼編碼器
     * @param employeeRepository 員工資料庫操作介面
     */
    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           PasswordEncoder passwordEncoder,
                           EmployeeRepository employeeRepository) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.employeeRepository = employeeRepository;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EmployeeDTO login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        Optional<Employee> employeeOpt = employeeRepository.findByUsername(loginDTO.getUsername());
        return employeeOpt.map(EmployeeDTO::new).orElse(null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EmployeeDTO getCurrentEmployee() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        Optional<Employee> employeeOpt = employeeRepository.findByUsername(username);
        return employeeOpt.map(EmployeeDTO::new).orElse(null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
               !"anonymousUser".equals(authentication.getPrincipal());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public String getUsernameFromToken(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();
        
        return claims.getSubject();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}