package com.ref.application.repository;

import com.ref.application.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByUserId(Long userId);

    UserEntity findByChatId(Long chatId);

    Optional<UserEntity> findByIndvRefCode(String refCode);

    boolean existsByChatId(Long chatId);

}
