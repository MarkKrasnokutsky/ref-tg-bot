package com.ref.application.service.handler;

import com.ref.application.config.Bot;
import com.ref.application.domain.enumeration.ECallbackData;
import com.ref.application.service.manager.InfoManager;
import com.ref.application.service.manager.InviteManager;
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
        }
        return null;
    }

}
