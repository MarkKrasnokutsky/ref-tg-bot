package com.mark.backend_application.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QRContent {
    /**
     * URL для редиректа
     */
    private String url;

    /**
     * Дополнительные данные
     */
    private Map<String, Object> data;

    /**
     * Метаданные
     */
    private Map<String, String> metadata;

    /**
     * Подпись/хеш для валидации (опционально)
     */
    private String signature;
}
