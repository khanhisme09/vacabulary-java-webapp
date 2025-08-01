//package com.vocabulary.controller;
//import com.vocabulary.model.Word;
//import com.vocabulary.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import java.security.Principal;
//import java.util.List;
//
//
//
//// UserController.java
//@Controller
//@RequestMapping("/user")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/save-word/{wordId}")
//    public String saveWord(@PathVariable Long wordId, Principal principal) {
//        String email = principal.getName();
//        userService.saveWordForUser(email, wordId);
//        return "redirect:/dictionary?success";
//    }
//
//    @GetMapping("/my-words")
//    public String myWordsPage(Model model, Principal principal) {
//        List<Word> words = userService.getSavedWords(principal.getName());
//        model.addAttribute("words", words);
//        return "my-words";
//    }
//}
