package com.familylink.backend.situation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubmitDescriptionRequest {

    @NotBlank(message = "Описание обязательно")
    @Size(min = 10, max = 5000, message = "Описание от 10 до 5000 символов")
    private String description;

    private boolean consentToAiAnalysis;
}