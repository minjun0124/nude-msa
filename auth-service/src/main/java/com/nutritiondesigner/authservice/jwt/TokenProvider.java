package com.nutritiondesigner.authservice.jwt;

import com.nutritiondesigner.authservice.model.dto.TokenDto;
import com.nutritiondesigner.authservice.service.RedisService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
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
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * 토큰의 생성, 토큰의 유효성 검증등을 담당
 * <p>
 * InitializingBean:afterPropertiesSet() - 빈이 생성되고 생성자 주입 이후 secret 값을 Base64 Decode해서 key변수 할당
 */
@Component
@RequiredArgsConstructor
@Slf4j
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

    /**
     * Token 정보를 이용해 Authentication 객체를 리턴
     */
    public Authentication getAuthentication(String token) {
        // 토큰으로 클레임 생성
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 클레임에서 권한 정보들을 추출출
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // 권한 정보를 이용해서 User 객체 생성
        User principal = new User(claims.getSubject(), "", authorities);

        // 유저 객체와 토큰, 권한정보를 이용해서 Authentication 객체를 리턴
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 토큰을 파싱해보고 발생하는 Exception 을 Catch, 문제 시 false, 정상이면 true
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public void blackAccessToken(String accessToken) {
        secret = env.getProperty("jwt.secret");
        redisService.setData(accessToken, "");

        Date expiration = Jwts.parserBuilder().setSigningKey(secret)
                .build().parseClaimsJws(accessToken).getBody().getExpiration();
        Long now = (new Date()).getTime();
        Long exp = expiration.getTime() - now;

        redisService.setDataExpire(accessToken, "", exp);
    }

    public void deleteRefreshToken(String accessToken) {
        String username = Jwts.parserBuilder().setSigningKey(secret)
                .build().parseClaimsJws(accessToken).getBody().getSubject();

        redisService.deleteData(username);
    }

    public Claims getClaims(String accessToken) {
        return Jwts.parserBuilder().setSigningKey(secret)
                .build().parseClaimsJws(accessToken).getBody();
    }

    public Boolean refreshTokenCheck(String username, String refreshToken) {

        Optional<String> tokenChk = Optional.ofNullable(redisService.getData(username));
        if (tokenChk.isEmpty()) {
            return false;
        }

        if (!refreshToken.equals(redisService.getData(username))) {
            return false;
        }

        return true;
    }

    public String reIssueAccessToken(Claims claims) {

        String username = claims.getSubject();
        String authorities = claims.get("auth", String.class);

        // this.tokenValidityInMilliseconds - application.yml 에 설정했던 만료시간
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        // jwt 토큰 생성
        return Jwts.builder()
                .setSubject(username)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }
}
