package com.ruilour.careerplanner.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("✅ SecurityConfig 已加载！");
        http
                // 禁用 CSRF（必须放在最前面）
                .csrf(csrf -> csrf.disable())
                // 禁用 Session（无状态）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 请求授权配置
                .authorizeHttpRequests(auth -> auth
                        // 放行所有页面和公开 API
                        .requestMatchers(
                                "/", "/login", "/play/**", "/videos/**", "/submit",
                                "/my-submissions", "/my-favorites",
                                "/admin/pending", "/admin/users/pending", "/admin/users/api/**",
                                "/career/**", "/checkin",
                                "/career-recommend/**", "/interview/**", "/resume/**",
                                "/api/auth/**", "/api/favorite/**", "/api/checkin/**",
                                "/career-recommend/api/**", "/interview/api/**", "/resume/api/**",
                                "/favicon.ico", "/ai-history", "/uploads/**",
                                "/images/**", "/css/**", "/js/**", "/error"
                        ).permitAll()
                         // 其他所有请求需要认证
                        .anyRequest().authenticated()
                )
                // 禁用表单登录和 HTTP Basic
                .formLogin(login -> login.disable())
                .httpBasic(basic -> basic.disable())
                // 添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}