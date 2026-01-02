package com.ref.application.service.handler;

import com.ref.application.config.Bot;
import com.ref.application.domain.enumeration.ECommandData;
import com.ref.application.service.manager.InfoManager;
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
    @Autowired
    private final InfoManager infoManager;

    // обработка команды
    public BotApiMethod<?> answer(Message message, Bot bot) {
        // получаем тип команды
        ECommandData command = ECommandData.fromString(message.getText());

        if (command == null) {
            return defaultAnswer(message);
        }

        // перечисление всех существующих команд и описание действия при нахождении той или иной команды
        switch (command) {
            case START -> {
                return start(message);
            }
            case INFO -> {
                return info(message);
            }
            default -> {
                return defaultAnswer(message);
            }
        }
    }

    private BotApiMethod<?> defaultAnswer(Message message) {
        // если такой команды не существует
        return SendMessage.builder()
                .chatId(message.getChatId())
                .text("""
                        Упс. Неизвестная команда :(
                        """)
                .build();
    }

    private BotApiMethod<?> start(Message message) {
        // передача ответственности manager'у данной команды
        return startManager.answerCommand(message);
    }

    private BotApiMethod<?> info(Message message) {
        // передача ответственности manager'у данной команды
        return infoManager.answerCommand(message);
    }

}
