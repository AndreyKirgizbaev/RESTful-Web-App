package ru.web.webapp.io.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.web.webapp.io.entity.PasswordResetTokenEntity;

public interface PasswordResetTokenRepository extends CrudRepository<PasswordResetTokenEntity, Long> {
    PasswordResetTokenEntity findByToken(String token);
}
