package com.familylink.backend.situation.exception;

public class SameRoleParticipantsException extends RuntimeException {
    public SameRoleParticipantsException() {
        super("Для AI-анализа нужны участники с разными ролями в семье. " +
                "Сейчас все участники с одной ролью.");
    }
}