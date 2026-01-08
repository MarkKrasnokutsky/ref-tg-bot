package com.ref.application;

import com.ref.application.config.TelegramProps;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
        "com.mark.refrabbitmq"
})
@ConfigurationPropertiesScan({
        "com.mark.refrabbitmq"
})
@EnableConfigurationProperties(TelegramProps.class)
@EnableRabbit
@EnableFeignClients(basePackages = "com.ref.application.client")
@EnableDiscoveryClient
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
