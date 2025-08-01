package com.vocabulary.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

// DictionaryApiResponse.java
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictionaryApiResponse {
    private String word;
    private String phonetic;
    private List<Meaning> meanings;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Meaning {
        private String partOfSpeech;
        private List<Definition> definitions;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Definition {
            private String definition;
            private String example;
            private List<String> synonyms;
        }
    }
}