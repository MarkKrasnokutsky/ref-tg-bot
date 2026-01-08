package com.mark.refrabbitmq.property;

import com.mark.refrabbitmq.property.nested.rabbit.Exchanges;
import com.mark.refrabbitmq.property.nested.rabbit.Queues;

public interface IRabbitProperty {

    Exchanges getExchanges();

    Queues getQueues();

}
