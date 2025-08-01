package com.vocabulary.controller;

import com.vocabulary.model.Word;
import com.vocabulary.service.DictionaryService;
import com.vocabulary.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequestMapping("/dictionary")
public class DictionaryController {

    private final DictionaryService dictionaryService;
    private final UserService userService;

    @Autowired
    public DictionaryController(DictionaryService dictionaryService, UserService userService) {
        this.dictionaryService = dictionaryService;
        this.userService = userService;
    }

    @GetMapping
    public String dictionaryPage(
            @RequestParam(name = "term", required = false) String term,
            Model model,
            Principal principal
    ) {
        if (term != null && !term.isEmpty()) {
            try {
                Word word = dictionaryService.lookupWord(term);
                model.addAttribute("word", word);

                if (principal != null) {
                    boolean isSaved = userService.getSavedWords(principal.getName())
                            .stream()
                            .anyMatch(w -> w.getId().equals(word.getId()));
                    model.addAttribute("isSaved", isSaved);
                }
            } catch (RuntimeException e) {
                model.addAttribute("error", e.getMessage());
            }
        }

        model.addAttribute("searchTerm", term == null ? "" : term);
        return "dictionary";
    }

    @PostMapping("/search")
    public String searchWord(
            @RequestParam String term,
            Model model,
            Principal principal
    ) {
        try {
            Word word = dictionaryService.lookupWord(term);
            model.addAttribute("word", word);

            // Kiểm tra xem người dùng đã lưu từ này chưa
            if (principal != null) {
                boolean isSaved = userService.getSavedWords(principal.getName())
                        .stream()
                        .anyMatch(w -> w.getId().equals(word.getId()));
                model.addAttribute("isSaved", isSaved);
            }

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("searchTerm", term);
        return "dictionary";
    }
}