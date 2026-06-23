package com.ruilour.careerplanner.config;

import com.ruilour.careerplanner.entity.Token;
import com.ruilour.careerplanner.mapper.TokenMapper;
import com.ruilour.careerplanner.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenMapper tokenMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 放行页面
        String requestUri = request.getRequestURI();
        if (requestUri.startsWith("/api/auth") ||
                requestUri.startsWith("/admin/users") ||
                requestUri.startsWith("/videos") ||
                requestUri.startsWith("/play") ||
                requestUri.startsWith("/checkin") ||
                requestUri.startsWith("/my-favorites") ||
                requestUri.startsWith("/career") ||
                requestUri.startsWith("/css") ||
                requestUri.startsWith("/js") ||
                requestUri.startsWith("/images") ||
                requestUri.startsWith("/img") ||
                requestUri.endsWith(".png") ||
                requestUri.endsWith(".jpg") ||
                requestUri.endsWith(".jpeg") ||
                requestUri.endsWith(".gif") ||
                requestUri.endsWith(".ico") ||
                requestUri.endsWith(".webp") ||
                requestUri.equals("/") ||
                requestUri.equals("/login") ||
                requestUri.equals("/career-recommend") ||
                requestUri.equals("/interview") ||
                requestUri.equals("/resume") ||
                requestUri.equals("/ai-history") ||
                requestUri.equals("/uploads") ||
                requestUri.equals("/pending") ||
                requestUri.equals("/interview/api") ||
                requestUri.equals("/resume/api") ||
                requestUri.equals("/submit") ||
                requestUri.equals("/my-submissions") ||
                requestUri.startsWith("/admin/pending")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. 获取请求头中的 Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. 判断是否存在且以 Bearer 开头
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 截取掉 "Bearer "

            try {
                // 3. 校验 JWT 是否过期（解析是否报错）
                if (!JwtUtil.isTokenExpired(token)) {
                    // 4. 从数据库中查询该 Token 是否有效（is_valid = 1）
                    Token dbToken = tokenMapper.selectByToken(token);
                    if (dbToken != null && dbToken.getIsValid() == 1) {
                        // 5. 解析用户信息，存入 Spring Security 上下文（让后续 @AuthenticationPrincipal 能用）
                        Long userId = JwtUtil.getUserIdFromToken(token);
                        String username = JwtUtil.getUsernameFromToken(token);

                        UserDetails userDetails = User.withUsername(username)
                                .password("")
                                .authorities(new ArrayList<>())
                                .build();

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        // 把 userId 也存进去，方便接口获取
                        authentication.setDetails(userId);

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                // Token 过期或解析失败，直接跳过，不设置认证（交给后续逻辑返回 401）
                System.out.println("Token 解析失败: " + e.getMessage());
            }
        }

        // 6. 继续执行后续过滤器
        filterChain.doFilter(request, response);
    }

}