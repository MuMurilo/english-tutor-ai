package com.english.tutor.infrastructure.repository;

import com.english.tutor.domain.ChatMessage;
import com.english.tutor.domain.ChatMessageRepository;
import com.english.tutor.infrastructure.persistence.ChatMessageEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ChatMessageRepositoryImpl implements ChatMessageRepository, PanacheRepository<ChatMessageEntity> {

    @Override
    @Transactional
    public void save(ChatMessage message) {
        ChatMessageEntity entity = ChatMessageEntity.fromDomain(message);
        if (entity.getId() == null) {
            persist(entity);
            message.setId(entity.getId()); // Sincroniza o ID de volta no domínio
        } else {
            getEntityManager().merge(entity);
        }
    }

    @Override
    public List<ChatMessage> findByUserId(Long userId) {
        return find("userId", userId).list().stream()
                .map(ChatMessageEntity::toDomain)
                .collect(Collectors.toList());
    }
}
