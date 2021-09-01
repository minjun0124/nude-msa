package com.nutritiondesigner.authservice.jwt;

import com.nutritiondesigner.authservice.service.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


/**
 * 토큰의 생성, 토큰의 유효성 검증등을 담당
 * <p>
 * InitializingBean:afterPropertiesSet() - 빈이 생성되고 생성자 주입 이후 secret 값을 Base64 Decode해서 key변수 할당
 */
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private final RedisService redisService;
    private static final String AUTHORITIES_KEY = "auth";

    private String secret;
    private long tokenValidityInMilliseconds;
    private Key key;

    private final Environment env;

    /**
     * Authentication 객체의 권한정보를 이용해서 Access 토큰을 생성하는 createAccessToken 메소드 추가
     */
    public String createAccessToken(Authentication authentication) {

        keyPropertiesSet("access");

        // Authentication 권한들
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // this.tokenValidityInMilliseconds - application.yml 에 설정했던 만료시간
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        // jwt 토큰 생성
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /**
     * Expire date 를 사용해서 Refresh 토큰을 생성하는 createRefreshToken 메소드 추가
     */
    public String createRefreshToken(String username) {

        keyPropertiesSet("refresh");

        // this.tokenValidityInMilliseconds - application.yml 에 설정했던 만료시간
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        // jwt 토큰 생성
        String rToken = Jwts.builder()
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();

        redisService.setData(username, rToken);

        return rToken;
    }

    public void keyPropertiesSet(String task) {
        secret = env.getProperty("jwt.secret");
        String validity = env.getProperty("jwt." + task + "-token-validity-in-seconds");
        tokenValidityInMilliseconds = Long.parseLong(validity) * 1000;
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }
}
