package com.familylink.backend.consent.dto;

import com.familylink.backend.consent.ConsentType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConsentRequest {

    @NotNull(message = "Тип согласия обязателен")
    private ConsentType consentType;

    @AssertTrue(message = "Необходимо явно согласиться")
    private boolean granted;
}