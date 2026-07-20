package com.familylink.backend.situation.exception;

public class RecommendationNotReadyException extends RuntimeException {
    public RecommendationNotReadyException(String message) {
        super(message);
    }
}