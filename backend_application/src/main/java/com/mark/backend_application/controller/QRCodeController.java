package com.mark.backend_application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mark.backend_application.domain.QRContent;
import com.mark.backend_application.domain.entity.QrCodeEntity;
import com.mark.backend_application.domain.request.ScanRequest;
import com.mark.backend_application.service.QrCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/qr")
@RequiredArgsConstructor
@Slf4j
public class QRCodeController {

    private final QrCodeService qrCodeService;

    private final ObjectMapper objectMapper;

    /**
     * Эндпоинт для обработки сканированного QR-кода
     * Принимает текст QR-кода, парсит его и делает редирект
     */
    @PostMapping("/scan")
    public ResponseEntity<?> processScannedQR(@RequestBody ScanRequest request) {
        try {
            // Парсим содержимое QR-кода
            QRContent qrContent = qrCodeService.parseQRContent(request.getQrText());

            // Логируем сканирование
            log.info("QR scanned: {}", qrContent.getUrl());
            log.info("QR data: {}", qrContent.getData());

            // Вариант 1: Редирект на URL
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, qrContent.getUrl())
                    .build();

            // Вариант 2: Возврат данных + URL для клиента
            /*
            Map<String, Object> response = new HashMap<>();
            response.put("redirectUrl", qrContent.getUrl());
            response.put("data", qrContent.getData());
            response.put("metadata", qrContent.getMetadata());
            return ResponseEntity.ok(response);
            */

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Неверный формат QR-кода", "message", e.getMessage()));
        }
    }

    /**
     * Парсинг QR-кода без редиректа (для анализа)
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
    public ResponseEntity<?> generateQRCode(@RequestBody Map<String, Object> data) {
        try {
            QrCodeEntity qrCode = qrCodeService.generateQRCode(data);

            Map<String, Object> response = new HashMap<>();
            response.put("id", qrCode.getId());
            response.put("fileName", qrCode.getFileName());
            response.put("downloadUrl", "/api/v1/qr/" + qrCode.getId() + "/image");
            response.put("scanUrl", "/api/v1/qr/scan"); // Эндпоинт для сканирования
            response.put("sampleQrContent", objectMapper.readValue(qrCode.getDataJson(), Map.class));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET запрос для получения изображения QR-кода по ID
     */
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getQRCodeImage(@PathVariable Long id) {
        try {
            QrCodeEntity qrCode = qrCodeService.getQRCodeById(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("filename", qrCode.getFileName());
            headers.setContentLength(qrCode.getQrImage().length);

            return new ResponseEntity<>(qrCode.getQrImage(), headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * GET запрос для получения информации о QR-коде по ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<QrCodeEntity> getQRCodeInfo(@PathVariable Long id) {
        try {
            QrCodeEntity qrCode = qrCodeService.getQRCodeById(id);
            return ResponseEntity.ok(qrCode);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * GET запрос для получения всех QR-кодов
     */
    @GetMapping
    public ResponseEntity<List<QrCodeEntity>> getAllQRCodes() {
        List<QrCodeEntity> qrCodes = qrCodeService.getAllQRCodes();
        return ResponseEntity.ok(qrCodes);
    }

    /**
     * DELETE запрос для удаления QR-кода
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQRCode(@PathVariable Long id) {
        qrCodeService.deleteQRCode(id);
        return ResponseEntity.noContent().build();
    }
}
