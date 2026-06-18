package com.english.tutor.infrastructure.repository;

import com.english.tutor.domain.User;
import com.english.tutor.domain.UserRepository;
import com.english.tutor.infrastructure.persistence.UserEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class UserRepositoryImpl implements UserRepository, PanacheRepository<UserEntity> {

    @Override
    @Transactional
    public void save(User user) {
        UserEntity entity = UserEntity.fromDomain(user);
        if (entity.getId() == null) {
            persist(entity);
            user.setId(entity.getId()); // Sincroniza o ID gerado de volta no modelo de domínio
        } else {
            getEntityManager().merge(entity);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return find("email", email).firstResultOptional().map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return findByIdOptional(id).map(UserEntity::toDomain);
    }
}
