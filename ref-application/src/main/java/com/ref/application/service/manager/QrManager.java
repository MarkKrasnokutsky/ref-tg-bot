package com.ref.application.service.manager;

import com.mark.backend_application.domain.response.QrCodeResponse;
import com.mark.refrabbitmq.domain.message.IndQrInfoMessage;
import com.ref.application.client.RefBackendApiClient;
import com.ref.application.config.Bot;
import com.ref.application.domain.entity.UserEntity;
import com.ref.application.domain.enumeration.ECallbackData;
import com.ref.application.repository.UserRepository;
import com.ref.application.service.IRefBackendApiService;
import com.ref.application.service.factory.AnswerMethodFactory;
import com.ref.application.service.factory.KeyboardFactory;
import com.ref.application.service.message.IEventUserQrGenMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.List;


@Component
@RequiredArgsConstructor
public class QrManager {

    private final IEventUserQrGenMessageService eventUserQrGenMessageService;
    private final UserRepository userRepository;
    private final IRefBackendApiService refBackendApiService;

    public BotApiMethod<?> answerCommand(Message message, Bot bot) {

        UserEntity currentUser = userRepository.findByChatId(message.getChatId());

        eventUserQrGenMessageService.sendUserQrGenToQueue(IndQrInfoMessage.builder()
                .balance(currentUser.getBalance())
                .username(currentUser.getUsername())
                .userId(currentUser.getUserId())
                .build());

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(message.getChatId());
        QrCodeResponse qrCodeResponse = refBackendApiService.getQRCodeInfoByOwnerId(message.getChatId());
        sendPhoto.setPhoto(new InputFile(
                new ByteArrayInputStream(refBackendApiService.getImageQrByOwnerId(message.getChatId())),
                qrCodeResponse.getFileName() != null ? qrCodeResponse.getFileName() : "article.jpg"
        ));

        try {
            bot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}
