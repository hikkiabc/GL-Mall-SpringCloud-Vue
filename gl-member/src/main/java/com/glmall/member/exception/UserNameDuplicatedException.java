package com.glmall.member.exception;

public class UserNameDuplicatedException extends RuntimeException {
    public UserNameDuplicatedException() {
        super("Username Exists");
    }
}
