package com.ref.application.service.manager;

import com.ref.application.config.Bot;
import com.ref.application.config.TelegramProps;
import com.ref.application.domain.entity.UserEntity;
import com.ref.application.domain.enumeration.EUserState;
import com.ref.application.repository.UserRepository;
import com.ref.application.service.BonusService;
import com.ref.application.service.UserStateService;
import com.ref.application.service.factory.AnswerMethodFactory;
import com.ref.application.service.factory.KeyboardFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Optional;

@Component
public class ReferralInputManager {

    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;
    private final UserRepository userRepository;
    private final UserStateService userStateService;
    private final BonusService bonusService;

    private final TelegramProps telegramProps;
    private final TelegramClient telegramClient;

    public ReferralInputManager(AnswerMethodFactory answerMethodFactory, KeyboardFactory keyboardFactory, UserRepository userRepository,
                                UserStateService userStateService, BonusService bonusService, TelegramProps telegramProps) {
        this.answerMethodFactory = answerMethodFactory;
        this.keyboardFactory = keyboardFactory;
        this.userRepository = userRepository;
        this.userStateService = userStateService;
        this.bonusService = bonusService;
        this.telegramProps = telegramProps;
        this.telegramClient = new OkHttpTelegramClient(telegramProps.getToken());
    }

    public void askIfHasReferralCode(Message message) throws TelegramApiException {
        Long userId = message.getChatId();

        // Устанавливаем состояние в БД
        userStateService.setUserState(userId, EUserState.AWAITING_REFERRAL_RESPONSE);

        SendMessage sendMessage = answerMethodFactory.getSendMessage(
                message.getChatId(),
                "Есть ли у вас код приглашения от друга?",
                keyboardFactory.createYesNoKeyboard()
        );

        telegramClient.execute(sendMessage);
    }

    public void handleYesResponse(Message message) throws TelegramApiException {
        Long userId = message.getChatId();

        // Проверяем, что пользователь действительно в этом состоянии
        if (!userStateService.hasState(userId, EUserState.AWAITING_REFERRAL_RESPONSE)) {
            handleUnexpectedState(message);
            return;
        }

        // Устанавливаем новое состояние в БД
        userStateService.setUserState(userId, EUserState.AWAITING_REFERRAL_CODE);

        SendMessage sendMessage = answerMethodFactory.getSendMessage(
                message.getChatId(),
                "Введите реферальный код",
                null
        );

        telegramClient.execute(sendMessage);
    }

    public void handleNoResponse(Message message) throws TelegramApiException {
        Long userId = message.getFrom().getId();

        // Очищаем состояние
        userStateService.clearUserState(userId);

        SendMessage sendMessage = answerMethodFactory.getSendMessage(
                message.getChatId(),
                "Хорошо, продолжим без реферального кода!",
                null
        );

        telegramClient.execute(sendMessage);
    }

    public void handleReferralCodeInput(Message message) throws TelegramApiException {
        Long userId = message.getFrom().getId();
        String referralCode = message.getText().trim();

        // Проверяем, что пользователь действительно в этом состоянии
        if (!userStateService.hasState(userId, EUserState.AWAITING_REFERRAL_CODE)) {
            handleUnexpectedState(message);
            return;
        }

        // Очищаем состояние пользователя
        userStateService.clearUserState(userId);

        // Ищем пользователя по реферальному коду
        Optional<UserEntity> referrerOpt = userRepository.findByIndvRefCode(referralCode);
        UserEntity currentUser = userRepository.findByUserId(userId);

        if (referrerOpt.isEmpty()) {
            SendMessage errorMessage = answerMethodFactory.getSendMessage(
                    message.getChatId(),
                    "❌ К сожалению, данного реферального кода не существует. Попробуйте ввести код еще раз.",
                    null
            );
            telegramClient.execute(errorMessage);

            // Снова предлагаем ввести код
            askForReferralCodeAgain(message);
            return;
        }

        // Проверяем, не использовал ли уже пользователь реферальный код
        if (currentUser.getParent() != null) {
            SendMessage alreadyUsedMessage = answerMethodFactory.getSendMessage(
                    message.getChatId(),
                    "❌ Вы уже использовали реферальный код ранее.",
                    null
            );
            telegramClient.execute(alreadyUsedMessage);
            return;
        }

        UserEntity referrer = referrerOpt.get();
        // Проверяем, не пытается ли пользователь использовать свой собственный код
        if (referrer.getUserId().equals(userId)) {
            SendMessage selfReferralMessage = answerMethodFactory.getSendMessage(
                    message.getChatId(),
                    "❌ Нельзя использовать свой собственный реферальный код!",
                    null
            );
            telegramClient.execute(selfReferralMessage);

            // Снова предлагаем ввести код
            askForReferralCodeAgain(message);
            return;
        }

        // Сохраняем реферера для пользователя
        currentUser.setParent(referrer);
        currentUser.setFriendRefCode(referralCode);
        userRepository.save(currentUser);

        // Начисляем бонусы
        bonusService.awardReferralBonus(currentUser, referrer);

        // Отправляем сообщение об успехе
        SendMessage successMessage = answerMethodFactory.getSendMessage(
                message.getChatId(),
                "✅ Реферальный код успешно применен! Вам начислено 200 приветственных бонусов!",
                null
        );
        telegramClient.execute(successMessage);

        // Уведомляем реферера о приглашении
        notifyReferrer(referrer, currentUser);
    }

    private void askForReferralCodeAgain(Message message) throws TelegramApiException {
        Long userId = message.getFrom().getId();

        // Устанавливаем состояние для повторного ввода
        userStateService.setUserState(userId, EUserState.AWAITING_REFERRAL_CODE);

        SendMessage sendMessage = answerMethodFactory.getSendMessage(
                message.getChatId(),
                "Попробуйте ввести реферальный код еще раз:",
                null
        );

        telegramClient.execute(sendMessage);
    }

    private void handleUnexpectedState(Message message) throws TelegramApiException {
        Long userId = message.getFrom().getId();

        // Очищаем любое некорректное состояние
        userStateService.clearUserState(userId);

        SendMessage errorMessage = answerMethodFactory.getSendMessage(
                message.getChatId(),
                "⚠️ Что-то пошло не так. Давайте начнем заново.",
                null
        );

        telegramClient.execute(errorMessage);
    }

    private void notifyReferrer(UserEntity referrer, UserEntity referredUser) throws TelegramApiException {
        String messageText = String.format(
                "🎉 Поздравляем! Пользователь %s успешно зарегистрировался по вашему реферальному коду! " +
                        "Вам начислено 200 бонусов!",
                referredUser.getFirstName() != null ? referredUser.getFirstName() : "Новый пользователь"
        );

        SendMessage notification = answerMethodFactory.getSendMessage(
                referrer.getChatId(),
                messageText,
                null
        );

        telegramClient.execute(notification);
    }
}
