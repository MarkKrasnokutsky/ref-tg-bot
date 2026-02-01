package com.mark.backend_application.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanResponse {
    private String redirectUrl;
    private Map<String, Object> data;
    private Map<String, String> metadata;
    private boolean shouldRedirect;
}
