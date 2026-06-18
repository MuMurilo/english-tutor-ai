package com.english.tutor.infrastructure.repository;

import com.english.tutor.domain.Feedback;
import com.english.tutor.domain.FeedbackRepository;
import com.english.tutor.infrastructure.persistence.FeedbackEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FeedbackRepositoryImpl implements FeedbackRepository, PanacheRepository<FeedbackEntity> {

    @Override
    @Transactional
    public void save(Feedback feedback) {
        FeedbackEntity entity = FeedbackEntity.fromDomain(feedback);
        if (entity.getId() == null) {
            persist(entity);
            feedback.setId(entity.getId()); // Sincroniza o ID de volta no domínio
        } else {
            getEntityManager().merge(entity);
        }
    }

    @Override
    public List<Feedback> findByUserId(Long userId) {
        return find("userId", userId).list().stream()
                .map(FeedbackEntity::toDomain)
                .collect(Collectors.toList());
    }
}
