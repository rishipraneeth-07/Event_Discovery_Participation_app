package com.college.eventapp.exception;

public class ResourceNotFoundException extends  RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
