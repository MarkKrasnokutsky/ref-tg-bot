package com.ref.application.service;

import com.ref.application.domain.entity.UserEntity;
import com.ref.application.domain.enumeration.EUserState;
import com.ref.application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserStateService {

    private final UserRepository userRepository;

    @Transactional
    public void setUserState(Long userId, EUserState state) {
        UserEntity user = userRepository.findByUserId(userId);
        if (user != null) {
            user.setState(state);
            userRepository.save(user);
        }
    }

    public EUserState getUserState(Long userId) {
        UserEntity user = userRepository.findByUserId(userId);
        return user != null ? user.getState() : EUserState.NONE;
    }

    @Transactional
    public void clearUserState(Long userId) {
        setUserState(userId, EUserState.NONE);
    }

    public boolean hasState(Long userId, EUserState state) {
        return getUserState(userId) == state;
    }
}
