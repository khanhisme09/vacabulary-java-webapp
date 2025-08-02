package com.vocabulary.controller;
import com.vocabulary.model.Word;
import com.vocabulary.repository.WordRepository;
import com.vocabulary.service.FlashcardService;
import com.vocabulary.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/learn")
public class LearningController {

    private final UserService userService;
    private final WordRepository wordRepository;

    private final FlashcardService flashcardService;

    @Autowired
    public LearningController(UserService userService, WordRepository wordRepository, FlashcardService flashcardService) {
        this.userService = userService;
        this.wordRepository = wordRepository;
        this.flashcardService = flashcardService;
    }

    @GetMapping("/flashcards")
    public String flashcards(@RequestParam(required = false) Long wordId, Model model, Principal principal) {
        if (principal == null) return "redirect:/login";

        List<Word> words = (wordId != null)
                ? Collections.singletonList(wordRepository.findById(wordId).orElseThrow(() -> new RuntimeException("Word not found")))
                : userService.getSavedWords(principal.getName());

        if (words.isEmpty()) return "redirect:/user/my-words?empty";

        model.addAttribute("word", words.get(0));
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

    @GetMapping
    public String learningHub(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();

        // Tính toán tiến độ tổng thể
        Map<Long, Integer> allProgress = userService.getAllWordProgress(email);
        double overallProgress = allProgress.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        // Lấy từ đề xuất (có tiến độ < 50)
        List<Word> allWords = userService.getSavedWords(email);
        List<Word> recommendedWords = allWords.stream()
                .filter(word -> {
                    Integer progress = allProgress.get(word.getId());
                    return progress == null || progress < 50;
                })
                .sorted(Comparator.comparingInt(word ->
                        allProgress.getOrDefault(word.getId(), 0)))
                .limit(3)
                .collect(Collectors.toList());

        model.addAttribute("overallProgress", (int) overallProgress);
        model.addAttribute("recommendedWords", recommendedWords);
        return "learning-hub";
    }

    @GetMapping("/next-flashcard")
    public String nextFlashcard(
            @RequestParam Long currentWordId,
            @RequestParam(required = false, defaultValue = "false") boolean mastered,
            Model model,
            Principal principal) {

        if (principal == null) return "redirect:/login";
        String email = principal.getName();

        Word nextWord = flashcardService.getNextFlashcard(email, currentWordId, mastered);
        if (nextWord == null) return "redirect:/user/my-words?empty";

        model.addAttribute("word", nextWord);
        model.addAttribute("progress", flashcardService.getProgressStats(email));
        return "flashcards";
    }

}
