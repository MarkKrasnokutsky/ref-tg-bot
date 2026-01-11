package com.ref.application.service.handler;

import com.ref.application.config.TelegramProps;
import com.ref.application.domain.enumeration.ECommandData;
import com.ref.application.service.manager.InfoManager;
import com.ref.application.service.manager.InviteManager;
import com.ref.application.service.manager.QrManager;
import com.ref.application.service.manager.StartManager;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class CommandHandler {

    @Autowired
    private final StartManager startManager;
    @Autowired
    private final InfoManager infoManager;
    @Autowired
    private InviteManager inviteManager;
    @Autowired
    private QrManager qrManager;

    private final TelegramClient telegramClient;

    private final TelegramProps telegramProps;

    public CommandHandler(StartManager startManager, InfoManager infoManager, InviteManager inviteManager, QrManager qrManager,
    TelegramProps telegramProps) {
        this.startManager = startManager;
        this.infoManager = infoManager;
        this.inviteManager = inviteManager;
        this.qrManager = qrManager;
        this.telegramProps = telegramProps;
        this.telegramClient = new OkHttpTelegramClient(telegramProps.getToken());
    }

    // обработка команды
    @SneakyThrows
    public void answer(Message message) {
        // получаем тип команды
        ECommandData command = ECommandData.fromString(message.getText());

        if (command == null) {
            defaultAnswer(message);
        }

        // перечисление всех существующих команд и описание действия при нахождении той или иной команды
        switch (command) {
            case START -> {
                start(message);
            }
            case INFO -> {
                info(message);
            }
            case INVITE -> {
                try {
                    invite(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
            case TAKE_QR -> {
                takeQr(message);
            }
            default -> {
                defaultAnswer(message);
            }
        }
    }

    private void defaultAnswer(Message message) throws TelegramApiException {
        // если такой команды не существует
        telegramClient.execute(SendMessage.builder()
                .chatId(message.getChatId())
                .text("""
                        Упс. Неизвестная команда :(
                        """)
                .build());
    }

    @SneakyThrows
    private void start(Message message) {
        // передача ответственности manager'у данной команды
        startManager.answerCommand(message);
    }

    @SneakyThrows
    private void info(Message message) {
        // передача ответственности manager'у данной команды
        infoManager.answerCommand(message);
    }

    private void invite(Message message) throws TelegramApiException {
        inviteManager.answerCommand(message);
    }

    private void takeQr(Message message) {
        qrManager.answerCommand(message);
    }
}
