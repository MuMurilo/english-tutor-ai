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
        
        User user2 = new User(1L, "@.", "password123", "BEGINNER");
        assertFalse(user2.isValid());

        User user3 = new User(1L, ".@a", "password123", "BEGINNER");
        assertFalse(user3.isValid());
    }

    @Test
    public void testEmptyEmail() {
        User user = new User(1L, "", "password123", "BEGINNER");
        assertFalse(user.isValid());
    }

    @Test
    public void testLongPassword() {
        // 73 characters password should be invalid
        String longPassword = "a".repeat(73);
        User user = new User(1L, "student@test.com", longPassword, "BEGINNER");
        assertFalse(user.isValid());

        // 72 characters password should be valid
        String edgePassword = "a".repeat(72);
        User user2 = new User(1L, "student@test.com", edgePassword, "BEGINNER");
        assertTrue(user2.isValid());
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
