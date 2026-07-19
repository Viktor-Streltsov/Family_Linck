package com.familylink.backend.mood.dto;

import com.familylink.backend.mood.MoodType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateMoodRequest {

    @NotNull(message = "Тип настроения обязателен")
    private MoodType moodType;

    @Size(max = 500, message = "Заметка не более 500 символов")
    private String note;

    private boolean visibleToFamily = true;
}