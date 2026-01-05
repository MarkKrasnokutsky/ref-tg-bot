package com.ref.application.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ref.application.domain.enumeration.EUserState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.validation.constraints.Min;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "\"user\"", indexes = {
        @Index(name = "idx_user_chat_id", columnList = "chat_id", unique = true),
        @Index(name = "idx_user_id", columnList = "user_id", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private EUserState state = EUserState.NONE;

    @Column(name = "chat_id", nullable = false, unique = true)
    private Long chatId;

    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @CreationTimestamp
    @Column(name = "registered_at", nullable = false, updatable = false)
    private Instant registeredAt = Instant.now();

    @Column(name = "indv_ref_code", nullable = false, unique = true, updatable = false)
    private String indvRefCode;

    @Column(name = "friend_ref_code")
    private String friendRefCode;

    @Column(name = "balance", nullable = false)
    private Integer balance = 0;

    @Column(name = "total_referrals", nullable = false)
    private int totalReferrals = 0;

    // Связь "многие к одному" - этот пользователь может иметь родителя
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_user_id", foreignKey = @ForeignKey(name = "fk_ref_user_id"))
    @JsonBackReference // предотвращает рекурсию при сериализации JSON
    private UserEntity parent;

    // Связь "один ко многим" - этот пользователь может быть родителем для других пользователей
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    @JsonManagedReference // обратная сторона @JsonBackReference
    private List<UserEntity> children = new ArrayList<>();

}

