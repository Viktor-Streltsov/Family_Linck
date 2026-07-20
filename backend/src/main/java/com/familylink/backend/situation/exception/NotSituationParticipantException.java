package com.familylink.backend.situation.exception;

public class NotSituationParticipantException extends RuntimeException {
    public NotSituationParticipantException() {
        super("Вы не являетесь участником этой ситуации");
    }
}