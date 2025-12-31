package com.ref.application.service.manager;

import com.ref.application.domain.entity.UserEntity;
import com.ref.application.domain.enumeration.ECallbackData;
import com.ref.application.repository.UserRepository;
import com.ref.application.service.factory.AnswerMethodFactory;
import com.ref.application.service.factory.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StartManager {

    @Autowired
    private final AnswerMethodFactory answerMethodFactory;

    @Autowired
    private final KeyboardFactory keyboardFactory;

    private final UserRepository userRepository;

    public SendMessage answerCommand(Message message) {

        UserEntity currentUser = UserEntity.builder()
                .userId(message.getFrom().getId())
                .chatId(message.getChatId())
                .username(message.getFrom().getFirstName())
                .firstName(message.getFrom().getFirstName())
                .lastName(message.getFrom().getLastName())
                .build();
        userRepository.save(currentUser);

        return answerMethodFactory.getSendMessage(
                message.getChatId(),
                """
                        🖖Приветствую, это бот реферальной системы Trinity
                        """,
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("Информация"),
                        List.of(1),
                        List.of(ECallbackData.INFO.toString())
                )
        );
    }

}
