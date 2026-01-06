package com.ref.application.service.handler;

import com.ref.application.config.Bot;
import com.ref.application.domain.enumeration.EUserState;
import com.ref.application.repository.UserRepository;
import com.ref.application.service.UserStateService;
import com.ref.application.service.manager.ReferralInputManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class MessageHandler {

    private final UserStateService userStateService;
    private final ReferralInputManager referralInputManager;

    public BotApiMethod<?> answer(Message message, Bot bot) {

        if (userStateService.hasState(message.getFrom().getId(), EUserState.AWAITING_REFERRAL_CODE)) {
            try {
                referralInputManager.handleReferralCodeInput(message, bot);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

}