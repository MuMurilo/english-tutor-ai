package com.english.tutor.domain;

import java.util.List;

public interface FeedbackRepository {
    void save(Feedback feedback);
    List<Feedback> findByUserId(Long userId);
}
