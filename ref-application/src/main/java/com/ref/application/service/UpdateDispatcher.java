package com.ref.application.service;

import com.ref.application.config.Bot;
import com.ref.application.service.handler.CallbackQueryHandler;
import com.ref.application.service.handler.CommandHandler;
import com.ref.application.service.handler.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateDispatcher {

    private final MessageHandler messageHandler;
    private final CommandHandler commandHandler;
    private final CallbackQueryHandler callbackQueryHandler;


    public BotApiMethod<?> distribute(Update update, Bot bot) {
        // если update является callback query, то передадим соответствующему обработчику данное сообщение
        if (update.hasCallbackQuery()) {
            return callbackQueryHandler.answer(update.getCallbackQuery(), bot);
        }
        // если update является message, то передадим соответствующему обработчику данное сообщение
        if (update.hasMessage()) {
            // достанем сам message
            Message message = update.getMessage();
            // проверка, что message - это команда
            if (message.hasText() && message.getText().startsWith("/")) {
                // передаём handler'у
                return commandHandler.answer(message, bot);
            }
            // передаем в противном случае handler'у для текстовых сообщений
            return messageHandler.answer(message, bot);
        }
        log.info("Unsupported update: " + update);
        return null;
    }
}
