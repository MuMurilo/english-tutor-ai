package com.english.tutor.infrastructure.security;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PasswordHasher {

    public String hash(String password) {
        if (password == null) {
            return null;
        }
        return BcryptUtil.bcryptHash(password);
    }

    public boolean verify(String plaintext, String hashed) {
        if (plaintext == null || hashed == null) {
            return false;
        }
        try {
            return BcryptUtil.matches(plaintext, hashed);
        } catch (Exception e) {
            return false;
        }
    }
}
