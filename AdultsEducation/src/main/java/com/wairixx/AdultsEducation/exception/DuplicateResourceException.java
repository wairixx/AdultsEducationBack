package com.wairixx.AdultsEducation.exception;
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String messageKey) { super(messageKey); }
}