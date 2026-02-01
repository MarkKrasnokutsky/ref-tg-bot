package com.mark.refrabbitmq.domain.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IndQrInfoMessage {

    private Long userId;
    private String username;
    private Integer balance;

}
