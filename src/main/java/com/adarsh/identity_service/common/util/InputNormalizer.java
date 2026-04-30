package com.adarsh.identity_service.common.util;

public final class InputNormalizer {

    private InputNormalizer() {}

    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
