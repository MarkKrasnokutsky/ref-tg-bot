package com.ref.application.config;

import com.ref.application.service.UpdateDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@Component
@Slf4j
public class Bot implements SpringLongPollingBot {

    private final UpdateDispatcher updateDispatcher;

    private final TelegramProps telegramProps;

    public Bot(TelegramProps telegramProps, UpdateDispatcher updateDispatcher) {
        this.telegramProps = telegramProps;
        this.updateDispatcher = updateDispatcher;
    }

    // Получение токена бота
    @Override
    public String getBotToken() {
        return telegramProps.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        log.warn("Providing UpdateDispatcher as consumer");
        return updateDispatcher;
    }

}
