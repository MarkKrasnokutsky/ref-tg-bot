package com.ref.application.service.manager;

import com.ref.application.config.Bot;
import com.ref.application.domain.entity.UserEntity;
import com.ref.application.domain.enumeration.EUserState;
import com.ref.application.repository.UserRepository;
import com.ref.application.service.BonusService;
import com.ref.application.service.UserStateService;
import com.ref.application.service.factory.AnswerMethodFactory;
import com.ref.application.service.factory.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReferralInputManager {

    private final AnswerMethodFactory answerMethodFactory;
    private final KeyboardFactory keyboardFactory;
    private final UserRepository userRepository;
    private final UserStateService userStateService;
    private final BonusService bonusService;

    public SendMessage askIfHasReferralCode(Message message, Bot bot) throws TelegramApiException {
        Long userId = message.getFrom().getId();

        // Устанавливаем состояние в БД
        userStateService.setUserState(userId, EUserState.AWAITING_REFERRAL_RESPONSE);

        SendMessage sendMessage = answerMethodFactory.getSendMessage(
                message.getChatId(),
                "Есть ли у вас код приглашения от друга?",
                keyboardFactory.createYesNoKeyboard()
        );

        bot.execute(sendMessage);
        return null;
    }

    public SendMessage handleYesResponse(Message message, Bot bot) throws TelegramApiException {
        Long userId = message.getFrom().getId();

        // Проверяем, что пользователь действительно в этом состоянии
        if (!userStateService.hasState(userId, EUserState.AWAITING_REFERRAL_RESPONSE)) {
            return handleUnexpectedState(message, bot);
        }

        // Устанавливаем новое состояние в БД
        userStateService.setUserState(userId, EUserState.AWAITING_REFERRAL_CODE);

        SendMessage sendMessage = answerMethodFactory.getSendMessage(
                message.getChatId(),
                "Введите реферальный код",
                null
        );

        bot.execute(sendMessage);
        return null;
    }

    public SendMessage handleNoResponse(Message message, Bot bot) throws TelegramApiException {
        Long userId = message.getFrom().getId();

        // Очищаем состояние
        userStateService.clearUserState(userId);

        SendMessage sendMessage = answerMethodFactory.getSendMessage(
                message.getChatId(),
                "Хорошо, продолжим без реферального кода!",
                null
        );

        bot.execute(sendMessage);
        return null;
    }

    public SendMessage handleReferralCodeInput(Message message, Bot bot) throws TelegramApiException {
        Long userId = message.getFrom().getId();
        String referralCode = message.getText().trim();

        // Проверяем, что пользователь действительно в этом состоянии
        if (!userStateService.hasState(userId, EUserState.AWAITING_REFERRAL_CODE)) {
            return handleUnexpectedState(message, bot);
        }

        // Очищаем состояние пользователя
        userStateService.clearUserState(userId);

        // Ищем пользователя по реферальному коду
        Optional<UserEntity> referrerOpt = userRepository.findByIndvRefCode(referralCode);

        if (referrerOpt.isEmpty()) {
            SendMessage errorMessage = answerMethodFactory.getSendMessage(
                    message.getChatId(),
                    "❌ К сожалению, данного реферального кода не существует. Попробуйте ввести код еще раз.",
                    null
            );
            bot.execute(errorMessage);

            // Снова предлагаем ввести код
            return askForReferralCodeAgain(message, bot);
        }

        UserEntity referrer = referrerOpt.get();
        UserEntity currentUser = userRepository.findByUserId(userId);

        // Проверяем, не пытается ли пользователь использовать свой собственный код
        if (referrer.getUserId().equals(userId)) {
            SendMessage selfReferralMessage = answerMethodFactory.getSendMessage(
                    message.getChatId(),
                    "❌ Нельзя использовать свой собственный реферальный код!",
                    null
            );
            bot.execute(selfReferralMessage);
            return selfReferralMessage;
        }

        // Проверяем, не использовал ли уже пользователь реферальный код
        if (currentUser.getParent() != null) {
            SendMessage alreadyUsedMessage = answerMethodFactory.getSendMessage(
                    message.getChatId(),
                    "❌ Вы уже использовали реферальный код ранее.",
                    null
            );
            bot.execute(alreadyUsedMessage);
            return alreadyUsedMessage;
        }

        // Сохраняем реферера для пользователя
        currentUser.setParent(referrer);
        userRepository.save(currentUser);

        // Начисляем бонусы
        bonusService.awardReferralBonus(currentUser, referrer);

        // Отправляем сообщение об успехе
        SendMessage successMessage = answerMethodFactory.getSendMessage(
                message.getChatId(),
                "✅ Реферальный код успешно применен! Вам начислено 200 приветственных бонусов!",
                null
        );
        bot.execute(successMessage);

        // Уведомляем реферера о приглашении
        notifyReferrer(referrer, currentUser, bot);

        return successMessage;
    }

    private SendMessage askForReferralCodeAgain(Message message, Bot bot) throws TelegramApiException {
        Long userId = message.getFrom().getId();

        // Устанавливаем состояние для повторного ввода
        userStateService.setUserState(userId, EUserState.AWAITING_REFERRAL_CODE);

        SendMessage sendMessage = answerMethodFactory.getSendMessage(
                message.getChatId(),
                "Попробуйте ввести реферальный код еще раз:",
                null
        );

        bot.execute(sendMessage);
        return null;
    }

    private SendMessage handleUnexpectedState(Message message, Bot bot) throws TelegramApiException {
        Long userId = message.getFrom().getId();

        // Очищаем любое некорректное состояние
        userStateService.clearUserState(userId);

        SendMessage errorMessage = answerMethodFactory.getSendMessage(
                message.getChatId(),
                "⚠️ Что-то пошло не так. Давайте начнем заново.",
                null
        );

        bot.execute(errorMessage);
        return errorMessage;
    }

    private void notifyReferrer(UserEntity referrer, UserEntity referredUser, Bot bot) throws TelegramApiException {
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

        bot.execute(notification);
    }
}
