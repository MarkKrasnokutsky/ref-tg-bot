package com.ref.application.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
public class Bot extends TelegramWebhookBot {


    private final TelegramProps telegramProps;

    public Bot(TelegramProps telegramProps) {
        super(telegramProps.getToken());
        this.telegramProps = telegramProps;
    }

    // Реализация метода стандартного поведения бота на любой отправленный объект на сервер
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    // Получение url бота
    @Override
    public String getBotPath() {
        return telegramProps.getPath();
    }

    // Получение никнейма бота
    @Override
    public String getBotUsername() {
        return telegramProps.getUsername();
    }

}
