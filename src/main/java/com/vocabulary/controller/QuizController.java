package com.vocabulary.controller;
import com.vocabulary.model.Word;
import com.vocabulary.service.QuizService;
import com.vocabulary.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final UserService userService;

    @Autowired
    public QuizController(QuizService quizService, UserService userService) {
        this.quizService = quizService;
        this.userService = userService;
    }

    @GetMapping
    public String quizPage(
            @RequestParam(value = "wordIds", required = false) List<Long> wordIds,
            Model model,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            String email = principal.getName();
            List<Long> quizWordIds = wordIds;

            // Nếu không có wordIds, sử dụng từ đã lưu của người dùng
            if (quizWordIds == null || quizWordIds.isEmpty()) {
                quizWordIds = userService.getSavedWords(email).stream()
                        .map(Word::getId)
                        .collect(Collectors.toList());
            }

            QuizService.Quiz quiz = quizService.generateQuiz(10, quizWordIds);
            model.addAttribute("quiz", quiz);
            return "quiz";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "quiz-error";
        }
    }

    @PostMapping("/submit")
    public String submitQuiz(
            @RequestParam Map<String, String> allParams,
            Model model,
            Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }

        QuizService.Quiz quiz = (QuizService.Quiz) model.getAttribute("quiz");

        int score = 0;
        List<QuizResult> results = new ArrayList<>();
        String email = principal.getName();

        for (QuizService.Question question : quiz.getQuestions()) {
            String userAnswer = allParams.get("q_" + question.getWordId());
            boolean isCorrect = question.getCorrectAnswer().equals(userAnswer);

            if (isCorrect) {
                score++;
                // Cập nhật tiến độ cho từ đúng
                userService.updateWordProgress(email, question.getWordId(), 100);
            }

            results.add(new QuizResult(question, userAnswer, isCorrect));
        }

        // Tính điểm phần trăm
        int percentage = (int) Math.round((score * 100.0) / quiz.getQuestions().size());

        model.addAttribute("score", score);
        model.addAttribute("total", quiz.getQuestions().size());
        model.addAttribute("percentage", percentage);
        model.addAttribute("results", results);

        return "quiz-results";
    }

    @Data
    private static class QuizResult {
        private final QuizService.Question question;
        private final String userAnswer;
        private final boolean correct;

        public QuizResult(QuizService.Question question, String userAnswer, boolean correct) {
            this.question = question;
            this.userAnswer = userAnswer;
            this.correct = correct;
        }

        // Getters
    }
}
