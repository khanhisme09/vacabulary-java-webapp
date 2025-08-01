package com.vocabulary.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(WordNotFoundException.class)
    public String handleWordNotFound(WordNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "dictionary";
    }

    @ExceptionHandler(DictionaryServiceException.class)
    public String handleDictionaryServiceException(DictionaryServiceException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "dictionary";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        logger.error("Unexpected error: {}", ex.getMessage());
        model.addAttribute("error", "An unexpected error occurred. Please try again.");
        return "error";
    }
}