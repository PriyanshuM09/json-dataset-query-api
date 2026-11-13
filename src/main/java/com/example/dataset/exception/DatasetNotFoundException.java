package com.example.dataset.exception;

public class DatasetNotFoundException extends RuntimeException {

    public DatasetNotFoundException(String message) {
        super(message);
    }
}