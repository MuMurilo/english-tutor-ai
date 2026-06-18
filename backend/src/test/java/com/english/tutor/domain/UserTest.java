package com.english.tutor.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testValidUser() {
        User user = new User(1L, "student@test.com", "password123", "BEGINNER");
        assertTrue(user.isValid());
    }

    @Test
    public void testInvalidEmail() {
        User user = new User(1L, "invalid-email", "password123", "BEGINNER");
        assertFalse(user.isValid());
    }

    @Test
    public void testEmptyEmail() {
        User user = new User(1L, "", "password123", "BEGINNER");
        assertFalse(user.isValid());
    }

    @Test
    public void testShortPassword() {
        User user = new User(1L, "student@test.com", "123", "BEGINNER");
        assertFalse(user.isValid());
    }

    @Test
    public void testInvalidEnglishLevel() {
        User user = new User(1L, "student@test.com", "password123", "FLUENT");
        assertFalse(user.isValid());
    }
}
