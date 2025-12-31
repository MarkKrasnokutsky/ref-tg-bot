package com.ref.application.service.handler;

import com.ref.application.config.Bot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@RequiredArgsConstructor
public class CallbackQueryHandler {

    public BotApiMethod<?> answer(CallbackQuery callbackQuery, Bot bot) {
        return null;
    }

}
