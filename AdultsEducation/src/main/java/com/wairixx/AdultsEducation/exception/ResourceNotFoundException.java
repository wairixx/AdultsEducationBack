package com.wairixx.AdultsEducation.exception;
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String messageKey) { super(messageKey); }
}