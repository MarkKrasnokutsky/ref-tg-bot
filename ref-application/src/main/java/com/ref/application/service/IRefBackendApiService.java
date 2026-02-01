package com.ref.application.service;

import com.mark.backend_application.domain.response.QrCodeResponse;

public interface IRefBackendApiService {

    byte[] getImageQrByOwnerId(Long ownerId);

    QrCodeResponse getQRCodeInfoByOwnerId(Long ownerId);

}
