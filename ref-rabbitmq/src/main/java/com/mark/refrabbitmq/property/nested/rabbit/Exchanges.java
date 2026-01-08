package com.mark.refrabbitmq.property.nested.rabbit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Exchanges {

    private ExchangeItem userQrGenEvent;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExchangeItem {

        private String name;

    }
}
