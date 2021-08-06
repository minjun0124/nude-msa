package com.nutritiondesigner.userservice.controller;

import com.nutritiondesigner.userservice.exception.UserNotFoundException;
import com.nutritiondesigner.userservice.model.domain.Greeting;
import com.nutritiondesigner.userservice.model.domain.User;
import com.nutritiondesigner.userservice.model.form.PwCheckForm;
import com.nutritiondesigner.userservice.model.form.SignUpForm;
import com.nutritiondesigner.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-service/users")
public class UserController {

    private final UserService userService;
    private final Greeting greeting;

    @GetMapping("/health-check")
    public String status() {
        return "It's Working in User Service";
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
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<User> getMyUserInfo() {
        Optional<User> userOp = userService.getMyUserWithAuthorities();
        if (userOp.isEmpty()) {
            throw new UserNotFoundException(String.format("User not found"));
        }

        return ResponseEntity.ok(userOp.get());
    }

    // PreAuthorize 활용
    // ADMIN Role 만이 접근할 수 있다.
    @GetMapping("/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<User> getUserInfo(@PathVariable String username) {
        Optional<User> userOp = userService.getUserWithAuthorities(username);
        if (userOp.isEmpty()) {
            throw new UserNotFoundException(String.format("User not found"));
        }

        return ResponseEntity.ok(userOp.get());
    }

    @PostMapping("/pwcheck")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity passwordCheck(@Valid @RequestBody PwCheckForm pwCheckForm) {
        if (userService.passwordCheck(pwCheckForm)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity modUser(@Valid @RequestBody SignUpForm signUpForm) {
        userService.modInfo(signUpForm);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity withdrawUser() {
        userService.withdraw();
        //TODO: 탈퇴 후 logout

        return new ResponseEntity<>(HttpStatus.OK);
    }
}