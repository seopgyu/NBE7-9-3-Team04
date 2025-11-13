package com.backend.global.security;

import com.backend.domain.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

//JWT 핵심 유틸 클래스
@Component
@RequiredArgsConstructor
@Getter
public class JwtTokenProvider {

    private final CustomUserDetailsService customUserDetailsService;

    @Value("${custom.jwt.secretPattern}")
    private String secretPattern;

    @Value("${custom.jwt.access-token.expire-time}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${custom.jwt.refresh-token.expire-time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretPattern.getBytes(StandardCharsets.UTF_8));
    }


    //토큰 공통 생성 로직
    public String generateToken(Long id, String email, Role role, long expireTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expireTime); //만료시간 설정

        return Jwts.builder()
                .subject(email) // Subject에 email 저장
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("userId", id) // ID는 별도 클레임으로 저장. 만약 수정 필요하면 수정 예정
                .claim("role", role.name())
                .signWith(key)
                .compact();
    }

    //AccessToken 생성 (Role 포함)
    public String generateAccessToken(Long id, String email, Role role) {
        return generateToken(id, email, role, ACCESS_TOKEN_EXPIRE_TIME);
    }

    //RefreshToken 생성 (Role 포함)
    public String generateRefreshToken(Long id, String email, Role role) {
         return generateToken(id, email, role, REFRESH_TOKEN_EXPIRE_TIME);
    }

    //claims 파싱
    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public Long getIdFromToken(String token) {
        Object id = parseClaims(token).get("userId");
        if (id == null) return null;
        return Long.valueOf(id.toString());
    }

    public Role getRoleFromToken(String token){
        Object role = parseClaims(token).get("role");
        return Role.valueOf(role.toString());
    }

    //토큰을 Security 인증 객체로 변환
    public Authentication getAuthentication(String token) {
        String email = getEmailFromToken(token);
        UserDetails user = customUserDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());

    }

    //유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts
                    .parser()
                    .verifyWith(key)
                    .build()
                    .parse(token);

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    //jwt는 생성 시 밀리초, 쿠키는 만료 시 초 사용하므로 1000나눠서 넘겨준다
    public long getAccessTokenExpireTime() {
        return ACCESS_TOKEN_EXPIRE_TIME / 1000;
    }

    public long getRefreshTokenExpireTime() {
        return REFRESH_TOKEN_EXPIRE_TIME / 1000;
    }

}
