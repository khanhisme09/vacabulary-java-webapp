package com.vocabulary.service;

import com.vocabulary.model.Word;
import com.vocabulary.repository.WordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FlashcardService {
    private static final int MASTERY_THRESHOLD = 80;
    private final UserService userService;
    private final WordRepository wordRepository;

    public FlashcardService(UserService userService, WordRepository wordRepository) {
        this.userService = userService;
        this.wordRepository = wordRepository;
    }

    public Word getNextFlashcard(String email, Long currentWordId, boolean mastered) {
        validateInput(email);

        if (mastered && currentWordId != null) {
            userService.updateWordProgress(email, currentWordId, 100);
        }

        Map<Long, Integer> progressMap = userService.getAllWordProgress(email);
        List<Word> words = userService.getSavedWords(email);

        if (words.isEmpty()) {
            log.warn("No words found for user: {}", email);
            return null;
        }

        return selectNextWord(words, progressMap, currentWordId);
    }

    private Word selectNextWord(List<Word> words, Map<Long, Integer> progressMap, Long currentWordId) {
        // Ưu tiên từ có progress thấp nhất và không phải từ hiện tại
        List<Word> candidates = words.stream()
                .filter(word -> !word.getId().equals(currentWordId))
                .sorted(Comparator.comparingInt(word ->
                        progressMap.getOrDefault(word.getId(), 0)
                ))
                .collect(Collectors.toList());

        if (!candidates.isEmpty()) {
            return candidates.get(0);
        }

        // Fallback: chọn ngẫu nhiên nếu tất cả đều là từ hiện tại
        Collections.shuffle(words);
        return words.get(0);
    }

    public Map<Long, Integer> getProgressStats(String email) {
        return Optional.ofNullable(userService.getAllWordProgress(email))
                .orElseGet(HashMap::new);
    }

    private void validateInput(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Invalid email");
        }
    }
}