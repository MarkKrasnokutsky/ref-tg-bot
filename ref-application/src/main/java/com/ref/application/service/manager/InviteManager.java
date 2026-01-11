package com.ref.application.service.manager;

import com.ref.application.config.TelegramProps;
import com.ref.application.domain.entity.UserEntity;
import com.ref.application.domain.enumeration.ECallbackData;
import com.ref.application.repository.UserRepository;
import com.ref.application.service.factory.AnswerMethodFactory;
import com.ref.application.service.factory.KeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class InviteManager {

    @Autowired
    private final AnswerMethodFactory answerMethodFactory;

    @Autowired
    private final KeyboardFactory keyboardFactory;

    private final UserRepository userRepository;

    private final TelegramClient telegramClient;

    private final TelegramProps telegramProps;

    public InviteManager(AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory, UserRepository userRepository, TelegramProps telegramProps) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userRepository = userRepository;
        this.telegramProps = telegramProps;
        this.telegramClient = new OkHttpTelegramClient(telegramProps.getToken());
    }

    public void answerCommand(Message message) throws TelegramApiException {
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
        telegramClient.execute(sendMessage);
    }

    public void answerCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        UserEntity user = userRepository.findByChatId(callbackQuery.getMessage().getChatId());
        // отдаем пользователю сообщение от бота

        SendMessage sendMessage = answerMethodFactory.getSendMessage(
                callbackQuery.getMessage().getChatId(),
                """
                        Ниже отправляем код, который можно переслать другу для регистрации
                        Кстати, напоминаем! Мы подарим 200 бонусов Вам за каждого приглашённого друга и 200 приветственных бонусов Вашему другу сразу после регистрации🙌🏻✨
                        `%s`
                        """.formatted(user.getIndvRefCode()),
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Информация", "Получить бонус"),
                        List.of(1, 1),
                        List.of(ECallbackData.INFO.toString(), ECallbackData.TAKE_BONUS.toString())
                )
        );
        sendMessage.setParseMode("Markdown");
        telegramClient.execute(sendMessage);

    }

}
