package com.nutritiondesigner.authservice.controller;

import com.nutritiondesigner.authservice.jwt.TokenProvider;
import com.nutritiondesigner.authservice.model.dto.TokenDto;
import com.nutritiondesigner.authservice.model.form.SignInForm;
import io.jsonwebtoken.Claims;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    private final TokenProvider tokenProvider;
    // 스프링 시큐리티의 인증에 대한 지원을 설정하는 몇가지 메소드를 가지고 있다.
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @PostMapping
    @Timed(value = "auth.authorize", longTask = true)
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody SignInForm signInForm) {

        // username, password 를 가지고 Authentication Token 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signInForm.getUsername(), signInForm.getPassword());

        // 생성된 토큰을 이용해서 authenticate 메소드가 실행이 될 때,
        // CustomUserDetailsService:loadUserByUsername 메소드가 실행, User 객체 생성
        // User 객체로 Authentication 객체 생성
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 위에서 생성된 인증정보인 authentication 을 기준으로 JWT Token 생성
        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(signInForm.getUsername());

        // TokenDto를 이용해서 ResponseBody 에도 넣어서 return
        TokenDto tokenDto = new TokenDto(accessToken, refreshToken);

        return new ResponseEntity<>(tokenDto, HttpStatus.OK);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity logout(@RequestHeader("Authorization") String accessToken) {
        tokenProvider.blackAccessToken(accessToken);
        tokenProvider.deleteRefreshToken(accessToken);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity reIssueToken(@RequestBody TokenDto tokenDto) {
        String accessToken = tokenDto.getAccessToken();
        String refreshToken = tokenDto.getRefreshToken();

        Claims claims = tokenProvider.getClaims(accessToken);
        String username = claims.getSubject();

        Boolean chkResult = tokenProvider.refreshTokenCheck(username, refreshToken);

        if (chkResult) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String reIssuedToken = tokenProvider.reIssueAccessToken(claims);

        return new ResponseEntity<>(new TokenDto(reIssuedToken, refreshToken), HttpStatus.OK);
    }

    /**
     * TODO: Find Id by email credential
     */
//    @PostMapping("/findId")
//    public ResponseEntity findId() {
//
//    }
    /**
     * TODO: password reissue by email credential
     */
//    @PostMapping("/findPw")
//    public ResponseEntity findPw() {
//
//    }
}
