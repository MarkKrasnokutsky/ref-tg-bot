package com.ref.application.service;

import com.ref.application.service.handler.CallbackQueryHandler;
import com.ref.application.service.handler.CommandHandler;
import com.ref.application.service.handler.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateDispatcher implements LongPollingSingleThreadUpdateConsumer {


    private final MessageHandler messageHandler;
    private final CommandHandler commandHandler;
    private final CallbackQueryHandler callbackQueryHandler;

    @Override
    public void consume(Update update) {
        System.out.println("ПРИШЛО СООБЩЕНИЕ");
        // если update является callback query, то передадим соответствующему обработчику данное сообщение
        if (update.hasCallbackQuery()) {
            callbackQueryHandler.answer(update.getCallbackQuery());
        }
        // если update является message, то передадим соответствующему обработчику данное сообщение
        if (update.hasMessage()) {
            // достанем сам message
            Message message = update.getMessage();
            // проверка, что message - это команда
            if (message.hasText() && message.getText().startsWith("/")) {
                // передаём handler'у
                commandHandler.answer(message);
            }
            // передаем в противном случае handler'у для текстовых сообщений
            messageHandler.answer(message);
        }
        log.info("Unsupported update: " + update);
    }
}
