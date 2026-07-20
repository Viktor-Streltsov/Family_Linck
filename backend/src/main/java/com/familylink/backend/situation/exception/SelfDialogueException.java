package com.familylink.backend.situation.exception;

public class SelfDialogueException extends RuntimeException {
    public SelfDialogueException() {
        super("Для AI-анализа нужны участники с разными ролями в семье " +
                "(например, родитель и ребёнок). Разговор одного человека с самим собой невозможен.");
    }
}