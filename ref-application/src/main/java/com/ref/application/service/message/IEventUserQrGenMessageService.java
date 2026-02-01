package com.ref.application.service.message;

import com.mark.refrabbitmq.domain.message.IndQrInfoMessage;

public interface IEventUserQrGenMessageService {

    void sendUserQrGenToQueue(IndQrInfoMessage msg);

}
