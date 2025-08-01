package com.vocabulary.controller;
import com.vocabulary.dto.SearchTerm;
import com.vocabulary.exception.DictionaryServiceException;
import com.vocabulary.exception.WordNotFoundException;
import com.vocabulary.model.Word;
import com.vocabulary.service.DictionaryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


// DictionaryController.java
@Controller
@RequestMapping("/dictionary")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    @Autowired
    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping
    public String dictionaryPage(Model model) {
        model.addAttribute("searchTerm", new SearchTerm());
        model.addAttribute("recentSearches", dictionaryService.getRecentSearches());
        return "dictionary";
    }

    @PostMapping("/search")
    public String searchWord(
            @ModelAttribute SearchTerm searchTerm,
            Model model,
            HttpSession session
    ) {
        try {
            Word word = dictionaryService.lookupWord(searchTerm.getTerm());
            model.addAttribute("word", word);

            // Lưu từ tìm kiếm gần đây
            dictionaryService.addToRecentSearches(searchTerm.getTerm(), session);

        } catch (WordNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        } catch (DictionaryServiceException e) {
            model.addAttribute("error", "Service temporarily unavailable. Please try again later.");
        }

        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("recentSearches", dictionaryService.getRecentSearches());
        return "dictionary";
    }
}
