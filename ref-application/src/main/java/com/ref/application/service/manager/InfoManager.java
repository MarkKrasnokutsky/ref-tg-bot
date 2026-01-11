package com.ref.application.service.manager;

import com.ref.application.config.TelegramProps;
import com.ref.application.domain.enumeration.ECallbackData;
import com.ref.application.service.factory.AnswerMethodFactory;
import com.ref.application.service.factory.KeyboardFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class InfoManager {

    private TelegramClient telegramClient;

    private final TelegramProps telegramProps;

    @Autowired
    private final AnswerMethodFactory answerMethodFactory;

    @Autowired
    private final KeyboardFactory keyboardFactory;

    public InfoManager(TelegramProps telegramProps, AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory) {
        this.telegramProps = telegramProps;
        this.telegramClient = new OkHttpTelegramClient(telegramProps.getToken());
        this.keyboardFactory = keyboardFactory;
        this.answerMethodFactory = answerMethodFactory;
    }

    public void answerCommand(Message message) throws TelegramApiException {
        // отдаем пользователю сообщение от бота
        telegramClient.execute(answerMethodFactory.getSendMessage(
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
        ));
    }

    public void answerCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        // отдаем пользователю сообщение от бота
        telegramClient.execute(answerMethodFactory.getEditMessageText(
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
                )));
    }

}
