package com.vocabulary.controller;

import com.vocabulary.model.User;
import com.vocabulary.model.Word;
import com.vocabulary.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @PostMapping("/save-word")
    public String saveWord(@RequestParam Long wordId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        userService.saveWordForUser(principal.getName(), wordId);
        return "redirect:/dictionary?wordSaved";
    }

    @GetMapping("/my-words")
    public String myWordsPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        List<Word> words = userService.getSavedWords(principal.getName());
        model.addAttribute("words", words);
        return "my-words";
    }

    @PostMapping("/remove-word")
    public String removeWord(@RequestParam Long wordId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        userService.removeWordFromUser(principal.getName(), wordId);
        return "redirect:/user/my-words";
    }
}