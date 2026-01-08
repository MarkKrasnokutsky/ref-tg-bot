package com.mark.backend_application.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeImageResponse {
    private byte[] image;
    private String fileName;
    private String contentType;
    private Long contentLength;
}
