package com.ref.application.client;

import com.mark.backend_application.domain.response.QrCodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(
        name = "ref-backend-application"
)
public interface RefBackendApiClient {

    @GetMapping("/api/v1/qr/{ownerId}/image")
    ResponseEntity<byte[]> getQRCodeImage(@PathVariable Long ownerId);

    @GetMapping("/api/v1/qr/{ownerId}")
    ResponseEntity<QrCodeResponse> getQRCodeInfoByOwnerId(@PathVariable Long ownerId);
}
