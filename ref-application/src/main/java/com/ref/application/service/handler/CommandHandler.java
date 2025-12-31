package com.ref.application.service.handler;

import com.ref.application.config.Bot;
import com.ref.application.domain.enumeration.ECommandData;
import com.ref.application.service.manager.StartManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@RequiredArgsConstructor
public class CommandHandler {

    @Autowired
    private final StartManager startManager;

    public BotApiMethod<?> answer(Message message, Bot bot) {
        ECommandData command = ECommandData.fromString(message.getText());

        if (command == null) {
            return defaultAnswer(message);
        }

        switch (command) {
            case START -> {
                return start(message);
            }
            default -> {
                return defaultAnswer(message);
            }
        }
    }

    private BotApiMethod<?> defaultAnswer(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("""
                        Упс. Неизвестная команда :(
                        """)
                .build();
    }

    private BotApiMethod<?> start(Message message) {
        return startManager.answerCommand(message);
    }

}
