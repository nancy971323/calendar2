package com.company.calendar.config;

import com.company.calendar.service.auth.AuthService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT認證過濾器
 * <p>
 * 攔截每個HTTP請求，檢查請求頭中是否包含有效的JWT令牌，並設置Spring Security上下文。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Spring應用上下文，用於延遲加載AuthService
     */
    private final ApplicationContext applicationContext;
    
    /**
     * 用戶詳情服務
     */
    private final UserDetailsService userDetailsService;
    
    /**
     * 構造函數
     * 
     * @param applicationContext Spring應用上下文
     * @param userDetailsService 用戶詳情服務
     */
    @Autowired
    public JwtAuthenticationFilter(ApplicationContext applicationContext, UserDetailsService userDetailsService) {
        this.applicationContext = applicationContext;
        this.userDetailsService = userDetailsService;
    }
    
    /**
     * 獲取AuthService的實例（延遲加載）
     * 
     * @return AuthService實例
     */
    private AuthService getAuthService() {
        return applicationContext.getBean(AuthService.class);
    }
    
    /**
     * 過濾請求
     * 
     * @param request HTTP請求
     * @param response HTTP響應
     * @param filterChain 過濾器鏈
     * @throws ServletException Servlet異常
     * @throws IOException IO異常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String jwt = parseJwt(request);
            if (jwt != null && getAuthService().validateToken(jwt)) {
                String username = getAuthService().getUsernameFromToken(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("無法設置用戶認證: " + e.getMessage(), e);
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 從請求中提取JWT令牌
     * 
     * @param request HTTP請求
     * @return JWT令牌，如果沒有則返回null
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        
        return null;
    }
}