package com.nutritiondesigner.userservice.controller;

import com.nutritiondesigner.userservice.jwt.JwtFilter;
import com.nutritiondesigner.userservice.jwt.TokenProvider;
import com.nutritiondesigner.userservice.model.dto.TokenDto;
import com.nutritiondesigner.userservice.model.form.SignInForm;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        // Authentication 객체를 SecurityContext 에 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 위에서 생성된 인증정보인 authentication 을 기준으로 JWT Token 생성
        String jwt = tokenProvider.createToken(authentication);

        // JWT 토큰을 Response header 에 넣어줌
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        // TokenDto를 이용해서 ResponseBody 에도 넣어서 return
        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }

    /**
     * TODO: JWT token Redis
     */
//    @PostMapping("/logout")
//    public ResponseEntity logout() {
//
//    }
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
