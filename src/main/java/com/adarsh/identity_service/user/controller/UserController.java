package com.adarsh.identity_service.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @GetMapping("/me")
    public Map<String,String> me(Authentication authentication){
        return Map.of("userId", authentication.getName());
    }
}
