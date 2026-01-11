package com.ref.application.service.factory;

import com.ref.application.domain.enumeration.ECallbackData;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class KeyboardFactory {

    // инициализация inline-клавиатуры
    public InlineKeyboardMarkup getInlineKeyboardMarkup(List<String> text, List<Integer> config, List<String> data) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>();
        int index = 0;

        for (Integer rowNum: config) {
            InlineKeyboardRow row = new InlineKeyboardRow();
            for (int i = 0; i < rowNum; i++) {
                if (index < text.size() && index < data.size()) {
                    InlineKeyboardButton button = InlineKeyboardButton.builder()
                            .text(text.get(index))
                            .callbackData(data.get(index))
                            .build();
                    row.add(button);
                    index += 1;
                }
            }
            keyboard.add(row);
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    public ReplyKeyboardMarkup getReplyKeyboardMarkup(List<String> text, List<Integer> config) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        int index = 0;
        for (Integer rowNum: config) {
            KeyboardRow row = new KeyboardRow();
            for (int i = 0; i < rowNum; i++) {
                KeyboardButton button = KeyboardButton.builder()
                        .text(text.get(index))
                        .build();
                row.add(button);
                index += 1;
            }
            keyboard.add(row);
        }
        return ReplyKeyboardMarkup.builder()
                        .keyboard(keyboard)
                                .build();
    }

    public InlineKeyboardMarkup createYesNoKeyboard() {
        return getInlineKeyboardMarkup(
                List.of("Да", "Нет"),
                List.of(1, 1),
                List.of(ECallbackData.YES.toString(), ECallbackData.NO.toString())
        );
    }

}

