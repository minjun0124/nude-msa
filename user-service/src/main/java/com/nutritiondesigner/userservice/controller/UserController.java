package com.nutritiondesigner.userservice.controller;

import com.nutritiondesigner.userservice.exception.UserNotFoundException;
import com.nutritiondesigner.userservice.model.domain.Greeting;
import com.nutritiondesigner.userservice.model.domain.User;
import com.nutritiondesigner.userservice.model.dto.UserDto;
import com.nutritiondesigner.userservice.model.form.PwCheckForm;
import com.nutritiondesigner.userservice.model.form.SignUpForm;
import com.nutritiondesigner.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final Greeting greeting;
    private final Environment env;

    @GetMapping("/health-check")
    public String status() {
        return String.format("It's Working in User Service"
                + ", port(local.server.port)=" + env.getProperty("local.server.port")
                + ", port(server.port)=" + env.getProperty("server.port")
                + ", token secret=" + env.getProperty("jwt.secret")
                + ", token expiration time=" + env.getProperty("jwt.token-validity-in-seconds"));
    }

    @GetMapping("/welcome")
    public String welcome() {
        return greeting.getMessage();
    }

    // UserDto 를 받아서 userService 의 signup 메소드를 호출
    @PostMapping
    public ResponseEntity<User> signup(@Valid @RequestBody SignUpForm signUpForm) {
        return ResponseEntity.ok(userService.signup(signUpForm));
    }

    // PreAuthorize 활용
    // USER Role 과 ADMIN Role 모두 접근할 수 있다.
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<UserDto> getMyUserInfo(@RequestHeader("Authorization") String jwt) {
        UserDto userDto = userService.getUserWithJwt(jwt);

        return ResponseEntity.ok(userDto);
    }

    // PreAuthorize 활용
    // ADMIN Role 만이 접근할 수 있다.
//    @GetMapping("/{username}")
//    public ResponseEntity<User> getUserInfo(@PathVariable String username) {
//        Optional<User> userOp = userService.getUserWithAuthorities(username);
//        if (userOp.isEmpty()) {
//            throw new UserNotFoundException(String.format("User not found"));
//        }
//
//        return ResponseEntity.ok(userOp.get());
//    }

//    @PostMapping("/pwcheck")
//    public ResponseEntity passwordCheck(@RequestHeader("userId") String userId, @Valid @RequestBody PwCheckForm pwCheckForm) {
//        if (userService.passwordCheck(userId, pwCheckForm)) {
//            return new ResponseEntity<>(HttpStatus.OK);
//        }
//
//        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//    }

//    @PutMapping
//    public ResponseEntity modUser(@RequestHeader("userId") String userId, @Valid @RequestBody SignUpForm signUpForm) {
//        userService.modInfo(signUpForm);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

//    @DeleteMapping
//    public ResponseEntity withdrawUser(@RequestHeader("userId") String userId) {
//        userService.withdraw(userId);
//        //TODO: 탈퇴 후 logout
//
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}