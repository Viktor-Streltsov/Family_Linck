package com.familylink.backend.family.exception;

public class AlreadyMemberException extends RuntimeException {
    public AlreadyMemberException() {
        super("Вы уже состоите в этой семье");
    }
}