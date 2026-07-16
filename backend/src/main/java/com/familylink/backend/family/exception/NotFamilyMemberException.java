package com.familylink.backend.family.exception;

public class NotFamilyMemberException extends RuntimeException {
    public NotFamilyMemberException() {
        super("Вы не являетесь участником этой семьи");
    }
}