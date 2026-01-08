package com.ref.application.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ECommandData {

    START("/start"),
    INFO("/info"),
    INVITE("/invite"),
    TAKE_BONUS("/take_bonus"),
    TAKE_QR("/take_qr");

    private final String command;

    public static ECommandData fromString(String text) {
        for (ECommandData cmd : ECommandData.values()) {
            if (cmd.command.equals(text)) {
                return cmd;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return command;
    }

}

