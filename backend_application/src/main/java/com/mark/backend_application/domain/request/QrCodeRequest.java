package com.mark.backend_application.domain.request;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class QrCodeRequest {

    private Long userId;
    private String username;
    private Integer balance;

    private Map<String, Object> data;

}
