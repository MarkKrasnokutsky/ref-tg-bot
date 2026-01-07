package com.ref.application.service.manager;

import com.ref.application.domain.enumeration.ECallbackData;
import com.ref.application.service.factory.AnswerMethodFactory;
import com.ref.application.service.factory.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InfoManager {

    @Autowired
    private final AnswerMethodFactory answerMethodFactory;

    @Autowired
    private final KeyboardFactory keyboardFactory;

    public SendMessage answerCommand(Message message) {
        // отдаем пользователю сообщение от бота
        return answerMethodFactory.getSendMessage(
                message.getChatId(),
                """
                        What is Lorem Ipsum?
                        Lorem Ipsum is simply dummy text of the printing and typesetting industry.
                        Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.
                        It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.
                        It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages,
                        and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
                        """,
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("\uD83E\uDD1D\uD83C\uDFFBПригласить друга", "Получить бонус"),
                        List.of(1, 1),
                        List.of(ECallbackData.INVITE.toString(), ECallbackData.TAKE_BONUS.toString())
                )
        );
    }

    public EditMessageText answerCallbackQuery(CallbackQuery callbackQuery) {
        // отдаем пользователю сообщение от бота
        return answerMethodFactory.getEditMessageText(
                callbackQuery,
                """
                        What is Lorem Ipsum?
                        Lorem Ipsum is simply dummy text of the printing and typesetting industry.
                        Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.
                        It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.
                        It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages,
                        and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.
                        """,
                keyboardFactory.getInlineKeyboardMarkup(
                        List.of("\uD83E\uDD1D\uD83C\uDFFBПригласить друга", "Получить бонус"),
                        List.of(1, 1),
                        List.of(ECallbackData.INVITE.toString(), ECallbackData.TAKE_BONUS.toString())
                ));
    }

}
