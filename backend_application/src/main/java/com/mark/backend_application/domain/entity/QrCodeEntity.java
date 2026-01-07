package com.mark.backend_application.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "qr_codes")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class QrCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "data_json", columnDefinition = "TEXT")
    private String dataJson;

    @Lob
    @Column(name = "qr_image")
    private byte[] qrImage;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType = "image/png";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "redirect_url")
    private String redirectUrl;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
