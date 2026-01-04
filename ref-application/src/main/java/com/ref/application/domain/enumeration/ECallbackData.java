package com.ref.application.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ECallbackData {
    INFO("info"),
    INVITE("invite");

    private final String callback;

    public static ECallbackData fromString(String text) {
        for (ECallbackData cd : ECallbackData.values()) {
            if (cd.callback.equals(text)) {
                return cd;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return callback;
    }
}