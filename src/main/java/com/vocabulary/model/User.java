package com.vocabulary.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_vocabulary",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id")
    )
    private Set<Word> savedWords = new HashSet<>();

    // Thêm trường để theo dõi tiến độ học
    @ElementCollection
    @MapKeyColumn(name = "word_id")
    @Column(name = "mastery_level")
    @CollectionTable(name = "user_word_progress", joinColumns = @JoinColumn(name = "user_id"))
    private Map<Long, Integer> wordProgress = new HashMap<>();
}