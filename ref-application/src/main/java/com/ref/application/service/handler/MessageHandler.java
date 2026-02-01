package com.ref.application.service.handler;

import com.ref.application.domain.enumeration.EUserState;
import com.ref.application.service.UserStateService;
import com.ref.application.service.manager.ReferralInputManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class MessageHandler {

    private final UserStateService userStateService;
    private final ReferralInputManager referralInputManager;

    @SneakyThrows
    public void answer(Message message) {

        if(message.hasText() && message.getText().equals("❌ Отменить")) {
            referralInputManager.cancelReferralCode(message);
        }

        // Отслеживаем состояние ожидания реферального кода от пользователя и передаем ответственность менеджеру обработки кода
        if (userStateService.hasState(message.getFrom().getId(), EUserState.AWAITING_REFERRAL_CODE)) {
            try {
                referralInputManager.handleReferralCodeInput(message);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

}