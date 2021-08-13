package com.nutritiondesigner.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

/*
    Spring security를 사용한다면 SecurityContextHolder에서 Authentication 객체로부터 User 정보를 가져올 수 있을 것이고
    Token 기반의 인증을 사용한다면 일반적으로 Token을 파싱한 결과를 Thread local에 담아둘 것이므로
    Thread local로부터 사용자 정보를 가져오는 구현을 아래에서 구현하면 될 것이다.
*/
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditConfig {
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of(UUID.randomUUID().toString());
    }
}
