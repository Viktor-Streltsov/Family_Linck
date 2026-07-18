package com.familylink.backend.consent.exception;

import com.familylink.backend.consent.ConsentType;

public class ConsentRequiredException extends RuntimeException {
    public ConsentRequiredException(ConsentType type) {
        super("Для этого действия требуется согласие: " + type.name() +
                ". Пожалуйста, дайте согласие через /api/consents");
    }
}