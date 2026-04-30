package com.adarsh.identity_service.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

    @Email
    @NotBlank
    String email,

    @NotBlank
    String password

) {}
