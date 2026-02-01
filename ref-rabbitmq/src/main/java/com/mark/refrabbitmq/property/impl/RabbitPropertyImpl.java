package com.mark.refrabbitmq.property.impl;

import com.mark.refrabbitmq.property.IRabbitProperty;
import com.mark.refrabbitmq.property.nested.rabbit.Exchanges;
import com.mark.refrabbitmq.property.nested.rabbit.Queues;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("rabbitmq")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class RabbitPropertyImpl implements IRabbitProperty {

    @NestedConfigurationProperty
    private Exchanges exchanges;

    @NestedConfigurationProperty
    private Queues queues;

}
