package com.vocabulary.service;
import com.vocabulary.model.Word;
import com.vocabulary.repository.WordRepository;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


@Service
public class QuizService {

    private final WordRepository wordRepository;
    private final Random random = new Random();

    @Autowired
    public QuizService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public Quiz generateQuiz(int questionCount, List<Long> wordIds) {
        List<Word> allWords;

        if (wordIds != null && !wordIds.isEmpty()) {
            // Sử dụng từ cụ thể nếu có
            allWords = wordRepository.findAllById(wordIds);
        } else {
            // Sử dụng tất cả từ
            allWords = wordRepository.findAll();
        }

        if (allWords.size() < 4) {
            throw new RuntimeException("Not enough words to generate quiz (minimum 4 required)");
        }

        Collections.shuffle(allWords);
        List<Word> selectedWords = allWords.subList(0, Math.min(questionCount, allWords.size()));

        Quiz quiz = new Quiz();
        for (Word word : selectedWords) {
            quiz.addQuestion(generateQuestion(word, allWords));
        }

        return quiz;
    }

    private Question generateQuestion(Word correctWord, List<Word> allWords) {
        Question question = new Question();
        question.setType(QuestionType.DEFINITION);
        question.setWordId(correctWord.getId());
        question.setQuestion("What is the definition of: " + correctWord.getTerm() + "?");
        question.setCorrectAnswer(correctWord.getDefinition());

        // Tạo các lựa chọn
        List<String> choices = new ArrayList<>();
        choices.add(correctWord.getDefinition());

        // Thêm 3 định nghĩa ngẫu nhiên khác
        List<Word> otherWords = new ArrayList<>(allWords);
        otherWords.remove(correctWord);
        Collections.shuffle(otherWords);

        for (int i = 0; i < 3 && i < otherWords.size(); i++) {
            choices.add(otherWords.get(i).getDefinition());
        }

        Collections.shuffle(choices);
        question.setChoices(choices);

        return question;
    }

    public enum QuestionType {
        DEFINITION, SYNONYM, EXAMPLE
    }

    @Getter
    public static class Quiz {
        private final List<Question> questions = new ArrayList<>();

        public void addQuestion(Question question) {
            questions.add(question);
        }

    }

    @Data
    public static class Question {
        private QuestionType type;
        private Long wordId;
        private String question;
        private List<String> choices;
        private String correctAnswer;

        // Getters and Setters
    }
}