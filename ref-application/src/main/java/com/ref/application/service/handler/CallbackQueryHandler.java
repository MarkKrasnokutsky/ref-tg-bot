package com.ref.application.service.handler;

import com.ref.application.config.Bot;
import com.ref.application.domain.enumeration.ECallbackData;
import com.ref.application.domain.enumeration.EUserState;
import com.ref.application.repository.UserRepository;
import com.ref.application.service.UserStateService;
import com.ref.application.service.manager.InfoManager;
import com.ref.application.service.manager.InviteManager;
import com.ref.application.service.manager.ReferralInputManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class CallbackQueryHandler {

    private final InfoManager infoManager;
    private final InviteManager inviteManager;
    private final UserRepository userRepository;
    private final ReferralInputManager referralInputManager;
    private final UserStateService userStateService;

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
            case TAKE_BONUS -> {
                try {
                    return referralInputManager.askIfHasReferralCode((Message) callbackQuery.getMessage(), bot);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case YES -> {
                if (userStateService.hasState(callbackQuery.getFrom().getId(), EUserState.AWAITING_REFERRAL_RESPONSE)) {
                    try {
                        return referralInputManager.handleYesResponse((Message) callbackQuery.getMessage(), bot);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            case NO -> {
                if (userStateService.hasState(callbackQuery.getFrom().getId(), EUserState.AWAITING_REFERRAL_RESPONSE)) {
                    try {
                        return referralInputManager.handleNoResponse((Message) callbackQuery.getMessage(), bot);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return null;
    }

}
