package com.vocabulary.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


// SearchTerm.java
@Data
public class SearchTerm {
    @NotBlank(message = "Please enter a word to search")
    private String term;
}