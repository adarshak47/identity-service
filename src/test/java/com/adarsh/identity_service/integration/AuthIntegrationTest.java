package com.adarsh.identity_service.integration;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/auth";
    }

    @Test
    void shouldRegisterAndLogin() {

        // ========================
        // COMMON HEADERS ✅
        // ========================
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ========================
        // REGISTER
        // ========================
        String registerBody = """
            {
                "email": "test@mail.com",
                "password": "password"
            }
        """;

        HttpEntity<String> registerRequest =
            new HttpEntity<>(registerBody, headers);

        ResponseEntity<String> registerResponse =
            restTemplate.postForEntity(
                baseUrl() + "/register",
                registerRequest,
                String.class
            );

        System.out.println("Register Response: " + registerResponse.getBody());

        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());

        // ========================
        // LOGIN
        // ========================
        String loginBody = """
            {
                "email": "test@mail.com",
                "password": "password"
            }
        """;

        HttpEntity<String> loginRequest =
            new HttpEntity<>(loginBody, headers);

        ResponseEntity<String> loginResponse =
            restTemplate.postForEntity(
                baseUrl() + "/login",
                loginRequest,
                String.class
            );

        System.out.println("Login Response: " + loginResponse.getBody());

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertTrue(loginResponse.getBody().contains("accessToken"));
    }
}
