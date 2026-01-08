package com.mark.backend_application.repository;

import com.mark.backend_application.domain.entity.QrCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QrCodeRepository extends JpaRepository<QrCodeEntity, Long> {

    Optional<QrCodeEntity> findByOwnerId(Long ownerId);

}
