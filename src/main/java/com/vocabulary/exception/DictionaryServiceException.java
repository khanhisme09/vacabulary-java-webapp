package com.vocabulary.exception;

public class DictionaryServiceException extends RuntimeException {
    public DictionaryServiceException(String message) {
        super(message);
    }
}