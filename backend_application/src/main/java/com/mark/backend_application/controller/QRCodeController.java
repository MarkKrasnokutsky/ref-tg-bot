package com.mark.backend_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mark.backend_application.domain.QRContent;
import com.mark.backend_application.domain.entity.QrCodeEntity;
import com.mark.backend_application.domain.request.QrCodeRequest;
import com.mark.backend_application.domain.request.ScanRequest;
import com.mark.backend_application.domain.response.QrCodeImageResponse;
import com.mark.backend_application.domain.response.QrCodeResponse;
import com.mark.backend_application.domain.response.ScanResponse;
import com.mark.backend_application.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashMap;
import java.util.HashMap;
import java.util.HashMap;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/qr")
@RequiredArgsConstructor
@Slf4j
public class QRCodeController {

    private final QrCodeService qrCodeService;

    /**
     * Обработка сканированного QR-кода
     */
    @PostMapping("/scan")
    public ResponseEntity<?> processScannedQR(@RequestBody ScanRequest request) {
        try {
            ScanResponse response = qrCodeService.processScannedQR(request.getQrText());

            if (response.isShouldRedirect()) {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, response.getRedirectUrl())
                        .build();
            } else {
                return ResponseEntity.ok(response);
            }

        } catch (RuntimeException e) {
            log.error("QR processing error", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid QR code format", "message", e.getMessage()));
        }
    }

    /**
     * Парсинг QR-кода без редиректа
     */
    @PostMapping("/parse")
    public ResponseEntity<QRContent> parseQR(@RequestBody ScanRequest request) {
        try {
            QRContent content = qrCodeService.parseQRContent(request.getQrText());
            return ResponseEntity.ok(content);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Генерация QR-кода
     */
    @PostMapping("/generate")
    public ResponseEntity<QrCodeResponse> generateQRCode(@RequestBody QrCodeRequest request) {
        try {
            QrCodeResponse response = qrCodeService.generateQRCode(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("QR generation error", e);
            return ResponseEntity.badRequest()
                    .body(QrCodeResponse.builder()
                            .qrContent(Map.of("error", e.getMessage()))
                            .build());
        }
    }

    /**
     * Получение изображения QR-кода по пользователю
     */
    @GetMapping("/{ownerId}/image")
    public ResponseEntity<byte[]> getQRCodeImage(@PathVariable Long ownerId) {
        try {
            QrCodeImageResponse imageResponse = qrCodeService.getQRCodeImageByOwnerId(ownerId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("filename", imageResponse.getFileName());
            headers.setContentLength(imageResponse.getContentLength());

            return new ResponseEntity<>(imageResponse.getImage(), headers, HttpStatus.OK);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Получение информации о QR-коде по пользователю
     */
    @GetMapping("/info/owner/{ownerId}")
    public ResponseEntity<QrCodeResponse> getQRCodeInfoByOwnerId(@PathVariable Long ownerId) {
        try {
            QrCodeResponse response = qrCodeService.getQRCodeInfoByOwnerId(ownerId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Получение информации о QR-коде
     */
    @GetMapping("/info/{id}")
    public ResponseEntity<QrCodeResponse> getQRCodeInfo(@PathVariable Long id) {
        try {
            QrCodeResponse response = qrCodeService.getQRCodeInfo(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Получение всех QR-кодов
     */
    @GetMapping
    public ResponseEntity<List<QrCodeEntity>> getAllQRCodes() {
        List<QrCodeEntity> responses = qrCodeService.getAllQRCodes();
        return ResponseEntity.ok(responses);
    }

    /**
     * Удаление QR-кода
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQRCode(@PathVariable Long id) {
        try {
            qrCodeService.deleteQRCode(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
