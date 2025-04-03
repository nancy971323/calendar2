package com.company.calendar.security;

import com.company.calendar.service.auth.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * JWT認證過濾器
 * <p>
 * 在請求到達控制器前，解析Authorization頭中的JWT令牌，並設置Security上下文中的認證信息。
 * </p>
 * 
 * @author YourName
 * @version 1.0
 * @since 2025-03-24
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final ApplicationContext applicationContext;
    
    /**
     * 構造函數
     * 
     * @param authService 認證服務
     * @param applicationContext 應用上下文
     */
    @Autowired
    public JwtAuthenticationFilter(AuthService authService, ApplicationContext applicationContext) {
        this.authService = authService;
        this.applicationContext = applicationContext;
    }
    
    /**
     * 獲取用戶詳情服務
     * 
     * @return 用戶詳情服務
     */
    private UserDetailsService getUserDetailsService() {
        return applicationContext.getBean(UserDetailsService.class);
    }

    /**
     * 過濾方法
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
            if (jwt != null) {
                String username = authService.validateToken(jwt);
                
                if (username != null) {
                    UserDetails userDetails = getUserDetailsService().loadUserByUsername(username);
                    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("無法設置用戶認證: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 從請求中解析JWT令牌
     * 
     * @param request HTTP請求
     * @return JWT令牌，如果不存在則返回null
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
} 