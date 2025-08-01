package com.vocabulary.service;

import com.vocabulary.model.User;
import com.vocabulary.model.Word;
import com.vocabulary.repository.UserRepository;
import com.vocabulary.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WordRepository wordRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       WordRepository wordRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.wordRepository = wordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        // Kiểm tra email đã tồn tại
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void saveWordForUser(String email, Long wordId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new RuntimeException("Word not found"));

        if (!user.getSavedWords().contains(word)) {
            user.getSavedWords().add(word);

            // Khởi tạo tiến độ học cho từ mới
            user.getWordProgress().put(wordId, 0);

            userRepository.save(user);
        }
    }

    public void removeWordFromUser(String email, Long wordId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new RuntimeException("Word not found"));

        if (user.getSavedWords().contains(word)) {
            user.getSavedWords().remove(word);
            user.getWordProgress().remove(wordId);
            userRepository.save(user);
        }
    }

    public List<Word> getSavedWords(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new ArrayList<>(user.getSavedWords());
    }

    public void updateWordProgress(String email, Long wordId, int progress) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getWordProgress().containsKey(wordId)) {
            user.getWordProgress().put(wordId, progress);
            userRepository.save(user);
        }
    }
}