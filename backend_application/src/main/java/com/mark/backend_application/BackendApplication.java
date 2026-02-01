package com.mark.backend_application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(
        scanBasePackages = {
                "com.mark.backend_application",
                "com.mark.refrabbitmq"  // ← Добавьте это
        }
)
@ConfigurationPropertiesScan({
        "com.mark.backend_application",
        "com.mark.refrabbitmq"  // ← И это
})
@EnableDiscoveryClient
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
