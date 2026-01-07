package com.mark.backend_application.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QrCodeResponse {

    private Long id;
    private String fileName;
    private LocalDateTime createdAt;
    private String downloadUrl;

}
