package com.ruilour.careerplanner.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {

    // 密钥（实际项目放配置文件，这里为了演示固定写死）
    private static final String SECRET = "ruil_career_planner_2026_secret_key";
    // 过期时间：7天（单位毫秒）
    private static final long EXPIRE = 1000 * 60 * 60 * 24 * 7;

    // 生成Token
    public static String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRE);

        return Jwts.builder()
                .setSubject(userId.toString())          // 存放用户ID
                .claim("username", username)           // 存放用户名
                .setIssuedAt(now)                      // 签发时间
                .setExpiration(expireDate)             // 过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET) // 加密算法
                .compact();
    }

    // 从Token中解析出用户ID
    public static Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    // 从Token中解析出用户名
    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("username");
    }

    // 校验Token是否过期/合法（用于拦截器）
    public static boolean isTokenExpired(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().before(new Date());
    }
}