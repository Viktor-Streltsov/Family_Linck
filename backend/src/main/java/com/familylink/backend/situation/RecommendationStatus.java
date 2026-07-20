package com.familylink.backend.situation;

public enum RecommendationStatus {
    PENDING,             // ждёт генерации
    GENERATED,           // сгенерирована
    FAILED,              // ошибка при генерации
    BLOCKED_SENSITIVE    // заблокирована из-за sensitive-контента
}
