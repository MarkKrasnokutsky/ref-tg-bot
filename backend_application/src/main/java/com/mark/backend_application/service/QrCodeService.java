package com.mark.backend_application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.mark.backend_application.domain.QRContent;
import com.mark.backend_application.domain.entity.QrCodeEntity;
import com.mark.backend_application.domain.request.QrCodeRequest;
import com.mark.backend_application.domain.response.QrCodeImageResponse;
import com.mark.backend_application.domain.response.QrCodeResponse;
import com.mark.backend_application.domain.response.ScanResponse;
import com.mark.backend_application.repository.QrCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrCodeService {

    private final QrCodeRepository qrCodeRepository;
    private final ObjectMapper objectMapper;

    private String baseUrl = "http://localhost:8070";

    /**
     * Генерация QR-кода
     */
    public QrCodeResponse generateQRCode(QrCodeRequest request) {
        try {

            // Создаем структурированный объект для QR-кода
            QRContent qrContent = QRContent.builder()
                    .url(baseUrl + "/" + request.getUserId() + "/" + request.getBalance())
                    .data(request.getData())
                    .metadata(Map.of(
                            "generatedAt", Instant.now().toString(),
                            "type", "redirect_with_data"
                    ))
                    .build();

            // Преобразуем в JSON
            String jsonContent = objectMapper.writeValueAsString(qrContent);

            // Генерируем QR-код
            byte[] qrImage = generateQRCodeImage(jsonContent, 300, 300);

            // Сохраняем в базу
            String fileName = "qr_" + request.getUserId() + "_" + Instant.now() + ".png";

            QrCodeEntity qrCode = QrCodeEntity.builder()
                    .dataJson(jsonContent)
                    .qrImage(qrImage)
                    .fileName(fileName)
                    .redirectUrl(baseUrl + "/" + request.getUserId() + "/" + 100)
                    .ownerId(request.getUserId())
                    .build();

            QrCodeEntity saved = qrCodeRepository.save(qrCode);

            // Создаем ответ
            return QrCodeResponse.builder()
                    .id(saved.getId())
                    .fileName(saved.getFileName())
                    .createdAt(saved.getCreatedAt())
                    .redirectUrl(saved.getRedirectUrl())
                    .ownerId(saved.getOwnerId())
                    .qrContent(objectMapper.readValue(saved.getDataJson(), Map.class))
                    .build();

        } catch (Exception e) {
            log.error("Error generating QR code", e);
            throw new RuntimeException("Failed to generate QR code: " + e.getMessage(), e);
        }
    }

    /**
     * Обработка сканирования QR-кода
     */
    public ScanResponse processScannedQR(String qrText) {
        try {
            QRContent qrContent = parseQRContent(qrText);

            log.info("QR scanned: {}", qrContent.getUrl());
            log.info("QR data: {}", qrContent.getData());

            return ScanResponse.builder()
                    .redirectUrl(qrContent.getUrl())
                    .data(qrContent.getData())
                    .metadata(qrContent.getMetadata())
                    .shouldRedirect(true)
                    .build();

        } catch (Exception e) {
            log.error("Error processing scanned QR", e);
            throw new RuntimeException("Failed to process QR code: " + e.getMessage(), e);
        }
    }

    /**
     * Парсинг QR-кода (только парсинг без логики)
     */
    public QRContent parseQRContent(String qrText) {
        try {
            return objectMapper.readValue(qrText, QRContent.class);
        } catch (Exception e) {
            log.warn("Failed to parse as QRContent, treating as plain URL");

            return QRContent.builder()
                    .url(qrText)
                    .data(Map.of("raw", qrText))
                    .metadata(Map.of("type", "plain_url"))
                    .build();
        }
    }

    /**
     * Получение изображения QR-кода
     */
    public QrCodeImageResponse getQRCodeImageByOwnerId(Long ownerId) {
        QrCodeEntity qrCode = getQRCodeByOwnerId(ownerId);

        return QrCodeImageResponse.builder()
                .image(qrCode.getQrImage())
                .fileName(qrCode.getFileName())
                .contentType(qrCode.getContentType())
                .contentLength((long) qrCode.getQrImage().length)
                .build();
    }


    /**
     * Получение информации о QR-коде
     */
    public QrCodeResponse getQRCodeInfoByOwnerId(Long ownerId) {
        QrCodeEntity qrCode = getQRCodeByOwnerId(ownerId);

        try {
            Map<String, Object> qrContent = objectMapper.readValue(qrCode.getDataJson(), Map.class);

            return QrCodeResponse.builder()
                    .id(qrCode.getId())
                    .fileName(qrCode.getFileName())
                    .createdAt(qrCode.getCreatedAt())
                    .redirectUrl(qrCode.getRedirectUrl())
                    .ownerId(qrCode.getOwnerId())
                    .qrContent(qrContent)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse QR content", e);
        }
    }

    /**
     * Получение информации о QR-коде
     */
    public QrCodeResponse getQRCodeInfo(Long id) {
        QrCodeEntity qrCode = getQRCodeById(id);

        try {
            Map<String, Object> qrContent = objectMapper.readValue(qrCode.getDataJson(), Map.class);

            return QrCodeResponse.builder()
                    .id(qrCode.getId())
                    .fileName(qrCode.getFileName())
                    .createdAt(qrCode.getCreatedAt())
                    .redirectUrl(qrCode.getRedirectUrl())
                    .ownerId(qrCode.getOwnerId())
                    .qrContent(qrContent)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse QR content", e);
        }
    }

    /**
     * Получение редирект URL из содержимого QR-кода
     */
    public String getRedirectUrlFromQR(String qrText) throws Exception {
        QRContent content = parseQRContent(qrText);
        return content.getUrl();
    }

    // Метод генерации изображения QR-кода
    private byte[] generateQRCodeImage(String text, int width, int height) throws Exception {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 2);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        return baos.toByteArray();
    }

    /**
     * Получение QR-кода по ownerID
     */
    public QrCodeEntity getQRCodeByOwnerId(Long ownerId) {
        return qrCodeRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("QR-код не найден с id пользователя: " + ownerId));
    }

    /**
     * Получение QR-кода по ID
     */
    public QrCodeEntity getQRCodeById(Long id) {
        return qrCodeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("QR-код не найден с id: " + id));
    }

    /**
     * Получение всех QR-кодов
     */
    public List<QrCodeEntity> getAllQRCodes() {
        return qrCodeRepository.findAll();
    }

    /**
     * Удаление QR-кода
     */
    public void deleteQRCode(Long id) {
        qrCodeRepository.deleteById(id);
    }
}
