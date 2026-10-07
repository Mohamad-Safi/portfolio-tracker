package com.portfoliotracker.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class PasswordConfigTest {

    private final PasswordEncoder encoder = new PasswordConfig().passwordEncoder();

    @Test
    void matchesReturnsTrueForTheSamePassword() {
        assertTrue(encoder.matches("secret1x", encoder.encode("secret1x")));
    }

    @Test
    void matchesReturnsFalseForADifferentPassword() {
        assertFalse(encoder.matches("wrong1x", encoder.encode("secret1x")));
    }

    @Test
    void encodeGivesADifferentHashEachTime() {
        assertNotEquals(encoder.encode("secret1x"), encoder.encode("secret1x"));
    }

}
