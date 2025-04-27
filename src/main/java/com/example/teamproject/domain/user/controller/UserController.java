package com.example.teamproject.domain.user.controller;

import com.example.teamproject.domain.user.dto.request.LoginDto;
import com.example.teamproject.domain.user.dto.request.SignupDto;
import com.example.teamproject.domain.user.dto.response.UserDto;
import com.example.teamproject.domain.user.entity.User;
import com.example.teamproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/team6/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupDto signupDto){
        return ResponseEntity.ok(userService.signup(signupDto));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginDto loginDto){
        return ResponseEntity.ok(userService.login(loginDto));
    }

}
