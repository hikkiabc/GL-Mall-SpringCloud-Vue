package com.glmall.member.exception;

public class PhoneDuplicatedException  extends RuntimeException{
    public PhoneDuplicatedException() {
        super("Phone exits");
    }
}
