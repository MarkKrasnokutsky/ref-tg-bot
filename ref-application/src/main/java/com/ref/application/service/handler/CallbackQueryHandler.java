package com.ref.application.service.handler;

import com.ref.application.config.Bot;
import com.ref.application.domain.enumeration.ECallbackData;
import com.ref.application.domain.enumeration.EUserState;
import com.ref.application.repository.UserRepository;
import com.ref.application.service.manager.InfoManager;
import com.ref.application.service.manager.InviteManager;
import com.ref.application.service.manager.ReferralInputManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class CallbackQueryHandler {

    private final InfoManager infoManager;
    private final InviteManager inviteManager;
    private final UserRepository userRepository;
    private final ReferralInputManager referralInputManager;

    public BotApiMethod<?> answer(CallbackQuery callbackQuery, Bot bot) {
        // получаем тип callback query
        ECallbackData callbackData = ECallbackData.fromString(callbackQuery.getData());

        if (callbackData == null) {
            throw new RuntimeException();
        }

        switch (callbackData) {
            case INFO -> {
                return infoManager.answerCallbackQuery(callbackQuery);
            }
            case INVITE -> {
                try {
                    return inviteManager.answerCallbackQuery(callbackQuery, bot);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case YES -> {
                if (userRepository.findByUserId(callbackQuery
                        .getFrom()
                        .getId()).getState() == EUserState.AWAITING_REFERRAL_RESPONSE) {
                    try {
                        return referralInputManager.handleYesResponse(callbackQuery.getMessage(), bot);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return null;
    }

}
