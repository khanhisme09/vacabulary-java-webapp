package com.vocabulary.service;

import com.vocabulary.model.Word;
import com.vocabulary.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class DictionaryService {

    private final WordRepository wordRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public DictionaryService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
        this.restTemplate = new RestTemplate();
    }

    public Word lookupWord(String term) {
        // Kiểm tra cache
        Optional<Word> cachedWord = wordRepository.findByTerm(term);
        if (cachedWord.isPresent()) return cachedWord.get();

        // Gọi API (đơn giản hóa)
        String url = "https://api.dictionaryapi.dev/api/v2/entries/en/" + term;

        try {
            // Thực tế cần xử lý JSON response
            // Giả sử trả về Word object
            Word word = new Word();
            word.setTerm(term);
            word.setDefinition("Definition for " + term);
            word.setPhonetic("/fəˌnetɪk/");

            return wordRepository.save(word);

        } catch (Exception e) {
            throw new RuntimeException("Error fetching word: " + e.getMessage());
        }
    }
}