package com.ref.application.service.handler;

import com.ref.application.domain.enumeration.ECallbackData;
import com.ref.application.domain.enumeration.EUserState;
import com.ref.application.repository.UserRepository;
import com.ref.application.service.UserStateService;
import com.ref.application.service.manager.InfoManager;
import com.ref.application.service.manager.InviteManager;
import com.ref.application.service.manager.ReferralInputManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class CallbackQueryHandler {

    private final InfoManager infoManager;
    private final InviteManager inviteManager;
    private final UserRepository userRepository;
    private final ReferralInputManager referralInputManager;
    private final UserStateService userStateService;

    @SneakyThrows
    public void answer(CallbackQuery callbackQuery) {
        // получаем тип callback query
        ECallbackData callbackData = ECallbackData.fromString(callbackQuery.getData());

        if (callbackData == null) {
            throw new RuntimeException();
        }

        switch (callbackData) {
            case INFO -> {
                infoManager.answerCallbackQuery(callbackQuery);
            }
            case INVITE -> {
                try {
                    inviteManager.answerCallbackQuery(callbackQuery);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case TAKE_BONUS -> {
                try {
                    referralInputManager.askIfHasReferralCode((Message) callbackQuery.getMessage());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case YES -> {
                if (userStateService.hasState(callbackQuery.getFrom().getId(), EUserState.AWAITING_REFERRAL_RESPONSE)) {
                    try {
                        referralInputManager.handleYesResponse((Message) callbackQuery.getMessage());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            case NO -> {
                if (userStateService.hasState(callbackQuery.getFrom().getId(), EUserState.AWAITING_REFERRAL_RESPONSE)) {
                    try {
                        referralInputManager.handleNoResponse((Message) callbackQuery.getMessage());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

}
