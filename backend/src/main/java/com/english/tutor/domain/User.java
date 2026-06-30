package com.english.tutor.domain;

public class User {
    private Long id;
    private String email;
    private String password;
    private String englishLevel; // "BEGINNER", "INTERMEDIATE", "ADVANCED"

    public User() {
    }

    public User(Long id, String email, String password, String englishLevel) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.englishLevel = englishLevel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEnglishLevel() {
        return englishLevel;
    }

    public void setEnglishLevel(String englishLevel) {
        this.englishLevel = englishLevel;
    }

    private static final java.util.regex.Pattern EMAIL_PATTERN = 
        java.util.regex.Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    public boolean isValid() {
        if (email == null || email.trim().isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
            return false;
        }
        if (password == null || password.trim().isEmpty() || password.length() < 6 || password.length() > 72) {
            return false;
        }
        if (englishLevel == null) {
            return false;
        }
        String lvl = englishLevel.toUpperCase();
        return lvl.equals("BEGINNER") || lvl.equals("INTERMEDIATE") || lvl.equals("ADVANCED");
    }
}
