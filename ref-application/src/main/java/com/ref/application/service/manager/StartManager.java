package com.ref.application.service.manager;

import com.ref.application.config.TelegramProps;
import com.ref.application.domain.entity.UserEntity;
import com.ref.application.domain.enumeration.ECallbackData;
import com.ref.application.repository.UserRepository;
import com.ref.application.service.factory.AnswerMethodFactory;
import com.ref.application.service.factory.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StartManager {

    @Autowired
    private final AnswerMethodFactory answerMethodFactory;

    @Autowired
    private final KeyboardFactory keyboardFactory;

    private final UserRepository userRepository;

    private final TelegramClient telegramClient;

    private final TelegramProps telegramProps;

    public StartManager(AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory, UserRepository userRepository,
                        TelegramProps telegramProps) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userRepository = userRepository;
        this.telegramProps = telegramProps;
        this.telegramClient = new OkHttpTelegramClient(telegramProps.getToken());
    }

    public void answerCommand(Message message) throws TelegramApiException {
        if (!userRepository.existsByChatId(message.getChatId())) {
            // сохраняем пользователя в нашу БД
            UserEntity currentUser = UserEntity.builder()
                    .userId(message.getFrom().getId())
                    .chatId(message.getChatId())
                    .username(message.getFrom().getFirstName())
                    .firstName(message.getFrom().getFirstName())
                    .lastName(message.getFrom().getLastName())
                    .indvRefCode(UUID.randomUUID().toString())
                    .balance(100)
                    .build();
            userRepository.save(currentUser);
        }

        // отдаем пользователю сообщение от бота
        telegramClient.execute(answerMethodFactory.getSendMessage(
                message.getChatId(),
                """
                        🖖Приветствую, это бот реферальной системы Trinity
                        """,
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Информация", "\uD83E\uDD1D\uD83C\uDFFBПригласить друга", "Получить бонус"),
                        List.of(2, 1),
                        List.of(ECallbackData.INFO.toString(), ECallbackData.INVITE.toString(), ECallbackData.TAKE_BONUS.toString())
                )
        ));
    }

}
