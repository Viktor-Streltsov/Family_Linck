package com.familylink.backend.situation.exception;

public class SituationNotFoundException extends RuntimeException {
    public SituationNotFoundException() {
        super("Ситуация не найдена");
    }
}