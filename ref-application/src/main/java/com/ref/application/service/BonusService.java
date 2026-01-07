package com.ref.application.service;

import com.ref.application.domain.entity.UserEntity;
import com.ref.application.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BonusService {

    private final UserRepository userRepository;

    @Transactional
    public void awardReferralBonus(UserEntity referredUser, UserEntity referrer) {
        // Начисляем бонусы приглашенному пользователю
        int referralBonus = 200;
        referredUser.setBalance(referredUser.getBalance() + referralBonus);

        // Начисляем бонусы пригласившему пользователю
        referrer.setBalance(referrer.getBalance() + referralBonus);
        referrer.setTotalReferrals(referrer.getTotalReferrals() + 1);

        // Сохраняем изменения
        userRepository.save(referredUser);
        userRepository.save(referrer);
    }

}
