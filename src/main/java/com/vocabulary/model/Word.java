package com.vocabulary.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String term;

    private String phonetic;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String definition;

    @Column(columnDefinition = "TEXT")
    private String example;

    @ElementCollection
    private List<String> synonyms = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private DictionarySource source = DictionarySource.USER;

    public enum DictionarySource {
        USER, OXFORD, CAMBRIDGE, API, MERRIAM
    }
}