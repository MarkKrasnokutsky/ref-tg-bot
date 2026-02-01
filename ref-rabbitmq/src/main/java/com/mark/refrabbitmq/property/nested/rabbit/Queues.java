package com.mark.refrabbitmq.property.nested.rabbit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Queues {

    private QueueItem userQrGenQueue;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueueItem {

        private String name;

    }
}
