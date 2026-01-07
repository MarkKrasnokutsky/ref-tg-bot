package com.mark.backend_application.domain.request;

import java.util.Map;

public class QrCodeRequest {

    private Map<String, Object> data;

    public QrCodeRequest() {}

    public QrCodeRequest(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
