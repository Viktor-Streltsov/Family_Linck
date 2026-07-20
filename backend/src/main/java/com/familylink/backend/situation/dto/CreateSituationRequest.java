package com.familylink.backend.situation.dto;

import com.familylink.backend.situation.SituationCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateSituationRequest {

    @NotBlank(message = "Название обязательно")
    @Size(min = 3, max = 200, message = "Название от 3 до 200 символов")
    private String title;

    @NotNull(message = "Категория обязательна")
    private SituationCategory category;
}