package com.mark.backend_application.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeResponse {

    private Long id;
    private String fileName;
    private LocalDateTime createdAt;
    private String redirectUrl;
    private String viewUrl;
    private Map<String, Object> qrContent;
    private Long ownerId;

}
