package com.ref.application.service.impl;

import com.mark.backend_application.domain.response.QrCodeResponse;
import com.ref.application.client.RefBackendApiClient;
import com.ref.application.service.IRefBackendApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefBackendApiServiceImpl implements IRefBackendApiService {

    private final RefBackendApiClient refBackendApiClient;

    @Override
    public byte[] getImageQrByOwnerId(Long ownerId) {
        return refBackendApiClient.getQRCodeImage(ownerId).getBody();
    }

    @Override
    public QrCodeResponse getQRCodeInfoByOwnerId(Long ownerId) {
        return refBackendApiClient.getQRCodeInfoByOwnerId(ownerId).getBody();
    }
}
