package com.ref.application.service;

import com.ref.application.config.TelegramProps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Optional;

@Service
@Slf4j
public class UnsupportedUpdateDispatcher {

    private final TelegramClient telegramClient;
    private final TelegramProps telegramProps;

    public UnsupportedUpdateDispatcher(TelegramProps telegramProps) {
        this.telegramProps = telegramProps;
        this.telegramClient = new OkHttpTelegramClient(telegramProps.getToken());
    }

    public void handleNonTextMessage(Message message) {
        Long chatId = message.getChatId();
        String userName = message.getFrom().getFirstName();

        String responseText = String.format(
                "Привет, %s! 👋\n" +
                        "Я пока не умею обрабатывать %s.\n" +
                        "Пожалуйста, отправьте мне текстовое сообщение или используйте команды из меню.",
                userName,
                getMessageTypeDescription(message)
        );

        sendResponse(chatId, responseText);
        log.info("Handled non-text message from user {} in chat {}: {}",
                userName, chatId, getMessageTypeDescription(message));
    }

    public void handleUnsupportedUpdate(Update update) {
        Optional<Long> chatId = extractChatId(update);

        if (chatId.isPresent()) {
            String responseText = "Извините, я не могу обработать этот тип запроса. 😕\n" +
                    "Пожалуйста, используйте текстовые сообщения или кнопки меню.";

            sendResponse(chatId.get(), responseText);
        }

        log.info("Received unsupported update type: {}", update.getClass().getSimpleName());
    }

    private String getMessageTypeDescription(Message message) {
        if (message.hasPhoto()) {
            return "фотографии 📸";
        }
        if (message.hasDocument()) {
            return "документы 📎";
        }
        if (message.hasAudio()) {
            return "аудио сообщения 🎵";
        }
        if (message.hasVideo()) {
            return "видео 🎬";
        }
        if (message.hasVoice()) {
            return "голосовые сообщения 🎤";
        }
        if (message.hasSticker()) {
            return "стикеры 😊";
        }
        if (message.hasContact()) {
            return "контакты 👤";
        }
        if (message.hasLocation()) {
            return "местоположения 📍";
        }
        return "этот тип сообщений";
    }

    private void sendResponse(Long chatId, String text) {
        try {
            telegramClient.execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .parseMode("HTML")
                    .build());
        } catch (TelegramApiException e) {
            log.error("Failed to send message to chat {}: {}", chatId, e.getMessage());
        }
    }

    private Optional<Long> extractChatId(Update update) {
        // Пытаемся извлечь chatId из различных частей update
        if (update.hasMessage()) {
            return Optional.of(update.getMessage().getChatId());
        }
        if (update.hasCallbackQuery()) {
            return Optional.of(update.getCallbackQuery().getMessage().getChatId());
        }
        if (update.hasEditedMessage()) {
            return Optional.of(update.getEditedMessage().getChatId());
        }
        if (update.hasChannelPost()) {
            return Optional.of(update.getChannelPost().getChatId());
        }
        if (update.hasEditedChannelPost()) {
            return Optional.of(update.getEditedChannelPost().getChatId());
        }
        if (update.hasInlineQuery()) {
            return Optional.of(update.getInlineQuery().getFrom().getId());
        }
        if (update.hasChosenInlineQuery()) {
            return Optional.of(update.getChosenInlineQuery().getFrom().getId());
        }
        if (update.hasShippingQuery()) {
            return Optional.of(update.getShippingQuery().getFrom().getId());
        }
        if (update.hasPreCheckoutQuery()) {
            return Optional.of(update.getPreCheckoutQuery().getFrom().getId());
        }

        return Optional.empty();
    }

}
