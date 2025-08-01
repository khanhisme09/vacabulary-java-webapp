package com.vocabulary.controller;
import com.vocabulary.model.Word;
import com.vocabulary.repository.WordRepository;
import com.vocabulary.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;


@Controller
@RequestMapping("/learn")
public class LearningController {

    private final UserService userService;
    private final WordRepository wordRepository;

    @Autowired
    public LearningController(UserService userService, WordRepository wordRepository) {
        this.userService = userService;
        this.wordRepository = wordRepository;
    }

    @GetMapping("/flashcards")
    public String flashcards(
            @RequestParam(required = false) Long wordId,
            Model model,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        List<Word> words;
        if (wordId != null) {
            // Học từ cụ thể
            Word word = wordRepository.findById(wordId)
                    .orElseThrow(() -> new RuntimeException("Word not found"));
            words = Collections.singletonList(word);
        } else {
            // Học tất cả từ đã lưu
            words = userService.getSavedWords(principal.getName());
        }

        if (words.isEmpty()) {
            return "redirect:/user/my-words?empty";
        }

        model.addAttribute("words", words);
        return "flashcards";
    }

    @PostMapping("/update-progress")
    @ResponseBody
    public ResponseEntity<?> updateProgress(
            @RequestParam Long wordId,
            @RequestParam int progress,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        userService.updateWordProgress(principal.getName(), wordId, progress);
        return ResponseEntity.ok().build();
    }
}
