package com.nutritiondesigner.userservice.service;

import com.nutritiondesigner.userservice.client.CartServiceClient;
import com.nutritiondesigner.userservice.exception.UserNotFoundException;
import com.nutritiondesigner.userservice.model.domain.Authority;
import com.nutritiondesigner.userservice.model.domain.User;
import com.nutritiondesigner.userservice.model.dto.UserDto;
import com.nutritiondesigner.userservice.model.form.SignUpForm;
import com.nutritiondesigner.userservice.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final Environment env;

    private final CartServiceClient cartServiceClient;

    /**
     * 회원가입을 수행하는 메소드
     */
    @Transactional
    public User signup(SignUpForm signUpForm) {
        // UserDto 로부터 username 을 가져와서 username 중복 가입을 막는다.
        if (userRepository.findOneWithAuthoritiesByUsername(signUpForm.getUsername()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }

        log.info("SignUpForm address : " + signUpForm.getAddress().toString());

        // 권한 정보를 만든다.
        //빌더 패턴의 장점
        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")         // 유저의 권한 정보
                .build();

        // 유저 정보를 만든다
        User user = User.builder()
                .username(signUpForm.getUsername())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .email(signUpForm.getEmail())
                .phone(signUpForm.getPhone())
                .address(signUpForm.getAddress())
                .authorities(Collections.singleton(authority))
                .activated(true)
                .build();

        // 유저, 권한 정보를 저장
        userRepository.save(user);

        // cart 저장
//        Cart cart = new Cart(user, 0);
//        cartRepository.save(cart);

        /**
         * Using RestTemplate
         */
//        String cartUrl = String.format(env.getProperty("cart_service.url"), user.getId());
//        restTemplate.postForEntity(cartUrl, null, Object.class);

        /**
         * Using a feign client
         * Feign exception handling
         */
//        try {
//            cartServiceClient.createCart(user.getId());
//        } catch (FeignException.FeignClientException ex) {
//            log.error(ex.getMessage());
//        }

        log.info(String.valueOf(user.getId()));

        /* ErrorDecoder */
        cartServiceClient.createCart(user.getId());

        return user;
    }

    // username 을 받아와서 해당하는 유저 객체와 권한 정보를 가져올 수 있다.
//    public Optional<User> getUserWithAuthorities(String userId) {
//        return userRepository.findOneWithAuthoritiesById(userId);
//    }

    // 현재 SecurityContext 에 저장이 되어 있는 username 에 대한 유저객체와 권한정보를 가져올 수 있다.
//    public Optional<User> getMyUserWithAuthorities() {
//        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
//    }

//    public Boolean passwordCheck(String userId, PwCheckForm passwordForm) {
//        Optional<User> userOp = userRepository.findOneWithAuthoritiesById(userId);
//        if (userOp.isEmpty()) {
//            return false;
//        }
//        User user = userOp.get();
//        if (passwordEncoder.matches(passwordForm.getPassword(), user.getPassword())) {
//            return true;
//        }
//
//        return false;
//    }

//    @Transactional
//    public void modInfo(String userId, SignUpForm signUpForm) {
//        User user = userRepository.findOneWithAuthoritiesById(userId).get();
//        String changePw = passwordEncoder.encode(signUpForm.getPassword());
//        user.updateInfo(signUpForm, changePw);
//    }
//
//    @Transactional
//    public void withdraw(String userId) {
//        User user = userRepository.findOneWithAuthoritiesById(userId).get();
//        user.withdraw();
//    }

    public UserDto getUserWithJwt(String jwt) {
        String subject = null;
        jwt = jwt.replace("Bearer", "");
        String property = env.getProperty("jwt.secret");

        try {
            subject = Jwts.parserBuilder().setSigningKey(property)
                    .build().parseClaimsJws(jwt).getBody().getSubject();
        } catch (Exception exception) {
            throw new UserNotFoundException("요청하신 유저 정보를 찾지 못했습니다.");
        }

        if (subject == null || subject.isEmpty()) {
            throw new UserNotFoundException("요청하신 유저 정보를 찾지 못했습니다.");
        }

        Optional<User> userOp = userRepository.findByUsername(subject);

        if (userOp.isEmpty()) {
            throw new UserNotFoundException("요청하신 유저 정보를 찾지 못했습니다.");
        }



        UserDto userDto = modelMapper.map(userOp.get(), UserDto.class);

        return userDto;
    }
}
