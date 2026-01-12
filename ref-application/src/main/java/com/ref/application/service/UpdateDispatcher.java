package com.ref.application.service;

import com.ref.application.service.handler.CallbackQueryHandler;
import com.ref.application.service.handler.CommandHandler;
import com.ref.application.service.handler.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@Component
@Slf4j
public class UpdateDispatcher implements LongPollingSingleThreadUpdateConsumer {

    private final MessageHandler messageHandler;
    private final CommandHandler commandHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final UnsupportedUpdateDispatcher unsupportedUpdateDispatcher;

    public UpdateDispatcher(MessageHandler messageHandler, CommandHandler commandHandler,
                            CallbackQueryHandler callbackQueryHandler, UnsupportedUpdateDispatcher unsupportedUpdateDispatcher) {
        this.messageHandler = messageHandler;
        this.commandHandler = commandHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.unsupportedUpdateDispatcher = unsupportedUpdateDispatcher;
    }

    @Override
    public void consume(Update update) {
        System.out.println("ПРИШЛО СООБЩЕНИЕ: " + update);

        try {
            if (update.hasCallbackQuery()) {
                callbackQueryHandler.answer(update.getCallbackQuery());
                return;
            }

            if (update.hasMessage()) {
                Message message = update.getMessage();

                if (message.hasText() && message.getText().startsWith("/")) {
                    commandHandler.answer(message);
                    return;
                }

                if (message.hasText()) {
                    messageHandler.answer(message);
                    return;
                }

                // Обработка неподдерживаемых типов сообщений
                unsupportedUpdateDispatcher.handleNonTextMessage(message);
                return;
            }

            // Обработка других неподдерживаемых типов обновлений
            unsupportedUpdateDispatcher.handleUnsupportedUpdate(update);

        } catch (Exception e) {
            log.error("Error processing update: {}", update, e);
        }
    }
}
