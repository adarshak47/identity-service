package com.adarsh.identity_service.auth.service;



import com.adarsh.identity_service.auth.domain.UserAccount;
import com.adarsh.identity_service.auth.domain.UserStatus;
import com.adarsh.identity_service.auth.dto.LoginRequest;
import com.adarsh.identity_service.auth.dto.LoginResponse;
import com.adarsh.identity_service.auth.dto.RegisterRequest;
import com.adarsh.identity_service.auth.dto.RegisterResponse;
import com.adarsh.identity_service.auth.exception.EmailAlreadyExistsException;
import com.adarsh.identity_service.auth.exception.InvalidCredentialsException;
import com.adarsh.identity_service.auth.repository.UserAccountRepository;
import com.adarsh.identity_service.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public RegisterResponse registerUser(RegisterRequest request) {

        if(repository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        UserAccount user = new UserAccount(UUID.randomUUID(), request.email(), passwordEncoder.encode(request.password()), UserStatus.ACTIVE);
        UserAccount savedUser = repository.save(user);
        return new RegisterResponse(savedUser.getId(), savedUser.getEmail(), "User registered successfully");
    }

    public LoginResponse login(LoginRequest request){

        UserAccount user = repository.findByEmail(request.email()).orElseThrow(InvalidCredentialsException::new);

        if(!passwordEncoder.matches(request.password(), user.getPasswordHash())){
            throw new InvalidCredentialsException();
        }

        String token= jwtTokenProvider.generateToken(user.getId().toString(), user.getEmail());

        return new LoginResponse(token, "Bearer");
    }
}
