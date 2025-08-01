package com.vocabulary.service;

import com.vocabulary.exception.WordNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.vocabulary.model.Word;
import com.vocabulary.repository.WordRepository;

import java.util.Optional;
import java.util.ArrayList;
import com.vocabulary.dto.DictionaryApiResponse;
import com.vocabulary.exception.DictionaryServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


// DictionaryService.java
@Service
public class DictionaryService {
    private static final Logger logger = LoggerFactory.getLogger(DictionaryService.class);
    private static final String DICTIONARY_API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";

    private final RestTemplate restTemplate;
    private final WordRepository wordRepository;

    public DictionaryService(WordRepository wordRepository) {
        this.restTemplate = new RestTemplate();
        this.wordRepository = wordRepository;
    }

    public Word lookupWord(String term) {
        // Kiểm tra trong cơ sở dữ liệu trước
        Optional<Word> existingWord = wordRepository.findByTerm(term);
        if (existingWord.isPresent()) {
            logger.info("Retrieved word from cache: {}", term);
            return existingWord.get();
        }

        try {
            // Gọi API từ điển
            logger.info("Fetching word from API: {}", term);
            DictionaryApiResponse[] responses = restTemplate.getForObject(
                    DICTIONARY_API_URL + term,
                    DictionaryApiResponse[].class
            );

            if (responses == null || responses.length == 0) {
                throw new WordNotFoundException("No definition found for: " + term);
            }

            // Chuyển đổi kết quả API thành Word entity
            Word word = convertApiResponseToWord(responses[0]);
            word.setSource(Word.DictionarySource.API);

            // Lưu vào cơ sở dữ liệu để cache
            return wordRepository.save(word);

        } catch (HttpClientErrorException.NotFound e) {
            throw new WordNotFoundException("Word not found: " + term);
        } catch (Exception e) {
            logger.error("Error fetching word from API: {}", e.getMessage());
            throw new DictionaryServiceException("Error fetching word definition");
        }
    }

    private Word convertApiResponseToWord(DictionaryApiResponse response) {
        Word word = new Word();
        word.setTerm(response.getWord());
        word.setPhonetic(response.getPhonetic());

        // Lấy định nghĩa đầu tiên
        if (response.getMeanings() != null && !response.getMeanings().isEmpty()) {
            DictionaryApiResponse.Meaning firstMeaning = response.getMeanings().get(0);
            if (firstMeaning.getDefinitions() != null && !firstMeaning.getDefinitions().isEmpty()) {
                DictionaryApiResponse.Meaning.Definition firstDefinition = firstMeaning.getDefinitions().get(0);
                word.setDefinition(firstDefinition.getDefinition());
                word.setExample(firstDefinition.getExample());

                // Lấy synonyms nếu có
                if (firstDefinition.getSynonyms() != null) {
                    word.setSynonyms(new ArrayList<>(firstDefinition.getSynonyms()));
                }
            }
        }

        return word;
    }

    // Trong DictionaryService.java

    private static final String RECENT_SEARCHES_KEY = "recentSearches";
    private static final int MAX_RECENT_SEARCHES = 10;

    // Thêm phương thức quản lý lịch sử tìm kiếm
    public void addToRecentSearches(String term, HttpSession session) {
        List<String> recentSearches = getRecentSearches();

        // Loại bỏ nếu đã tồn tại
        recentSearches.remove(term);

        // Thêm vào đầu danh sách
        recentSearches.add(0, term);

        // Giới hạn số lượng
        if (recentSearches.size() > MAX_RECENT_SEARCHES) {
            recentSearches = recentSearches.subList(0, MAX_RECENT_SEARCHES);
        }

        session.setAttribute(RECENT_SEARCHES_KEY, recentSearches);
    }

    public List<String> getRecentSearches() {
        List<String> recentSearches = (List<String>) session.getAttribute(RECENT_SEARCHES_KEY);
        return recentSearches != null ? recentSearches : new ArrayList<>();
    }
}