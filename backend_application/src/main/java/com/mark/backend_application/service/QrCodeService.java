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

    /**
     * Генерация QR-кода с URL и данными
     */
    public QrCodeEntity generateQRCode(Map<String, Object> requestData) throws Exception {

        // Извлекаем URL для редиректа
        String redirectUrl = null;

        if (requestData.containsKey("redirect_url")) {
            redirectUrl = requestData.get("redirect_url").toString();
        }

        Long ownerId = null;

        if (requestData.containsKey("owner_id")) {
            ownerId = Long.parseLong(requestData.get("owner_id").toString());
        }

        // Создаем структурированный объект для QR-кода
        QRContent qrContent = QRContent.builder()
                .url(redirectUrl)
                .data(requestData)
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
        String fileName = "qr_" + ownerId + "_" + Instant.now().toEpochMilli() + ".png";

        QrCodeEntity qrCode = new QrCodeEntity();
        qrCode.setDataJson(jsonContent);
        qrCode.setQrImage(qrImage);
        qrCode.setFileName(fileName);
        qrCode.setRedirectUrl(redirectUrl);
        qrCode.setOwnerId(ownerId);


        return qrCodeRepository.save(qrCode);
    }

    /**
     * Парсинг QR-кода (для мобильного приложения или обработчика)
     */
    public QRContent parseQRContent(String qrText) throws Exception {
        try {
            // Пытаемся распарсить как наш формат
            return objectMapper.readValue(qrText, QRContent.class);
        } catch (Exception e) {
            log.warn("Не удалось распарсить как QRContent, возможно это простой URL");

            // Если не наш формат, возможно это просто URL
            QRContent content = new QRContent();
            content.setUrl(qrText);
            content.setData(Map.of("raw", qrText));
            content.setMetadata(Map.of("type", "plain_url"));

            return content;
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
