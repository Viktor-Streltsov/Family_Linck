package com.familylink.backend.situation;

public enum SituationStatus {
    OPEN,           // создана, ждём описаний
    IN_DISCUSSION,  // все описали свою сторону, идёт обсуждение
    RESOLVED,       // решена
    CLOSED          // закрыта без разрешения
}
