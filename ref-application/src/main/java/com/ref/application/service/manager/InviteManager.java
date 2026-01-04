package com.ref.application.service.manager;

import com.ref.application.config.Bot;
import com.ref.application.domain.entity.UserEntity;
import com.ref.application.repository.UserRepository;
import com.ref.application.service.factory.AnswerMethodFactory;
import com.ref.application.service.factory.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class InviteManager {

    @Autowired
    private final AnswerMethodFactory answerMethodFactory;

    @Autowired
    private final KeyboardFactory keyboardFactory;

    private final UserRepository userRepository;

    public BotApiMethod<?> answerCommand(Message message, Bot bot) throws TelegramApiException {
        UserEntity user = userRepository.findByUserId(message.getFrom().getId());
        // отдаем пользователю сообщение от бота

        SendMessage sendMessage = answerMethodFactory.getSendMessage(
                message.getChatId(),
                """
                        Ниже отправляем код, который можно переслать другу для регистрации
                        Кстати, напоминаем! Мы подарим 200 бонусов Вам за каждого приглашённого друга и 200 приветственных бонусов Вашему другу сразу после регистрации🙌🏻✨
                        ```%s```
                        """.formatted(user.getIndvRefCode()),
                null
        );
        sendMessage.setParseMode("Markdown");
        bot.execute(sendMessage);

        return null;
    }

    public EditMessageText answerCallbackQuery(CallbackQuery callbackQuery, Bot bot) throws TelegramApiException {
        UserEntity user = userRepository.findByUserId(callbackQuery.getMessage().getFrom().getId());
        // отдаем пользователю сообщение от бота

        SendMessage sendMessage = answerMethodFactory.getSendMessage(
                callbackQuery.getMessage().getChatId(),
                """
                        Ниже отправляем код, который можно переслать другу для регистрации
                        Кстати, напоминаем! Мы подарим 200 бонусов Вам за каждого приглашённого друга и 200 приветственных бонусов Вашему другу сразу после регистрации🙌🏻✨
                        ```%s```
                        """.formatted(user.getIndvRefCode()),
                null
        );
        sendMessage.setParseMode("Markdown");
        bot.execute(sendMessage);

        return null;
    }

}
