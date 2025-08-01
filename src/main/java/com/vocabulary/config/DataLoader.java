//package com.vocabulary.config;
//
//import com.vocabulary.model.Word;
//import com.vocabulary.repository.WordRepository;
//import jakarta.annotation.PostConstruct;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class DataLoader {
//
//    private final WordRepository wordRepository;
//
//    public DataLoader(WordRepository wordRepository) {
//        this.wordRepository = wordRepository;
//    }
//
//    @PostConstruct
//    public void loadSampleData() {
//        if (wordRepository.count() == 0) {
//            // Add sample words
//            wordRepository.save(new Word(null, "abundant", "/əˈbʌndənt/",
//                    "Existing or available in large quantities; plentiful.",
//                    "The region has abundant natural resources.",
//                    List.of("plentiful", "copious", "ample")));
//
//            wordRepository.save(new Word(null, "diligent", "/ˈdɪlɪdʒənt/",
//                    "Having or showing care and conscientiousness in one's work or duties.",
//                    "She is a diligent worker who always completes her tasks on time.",
//                    List.of("industrious", "assiduous", "meticulous")));
//
//            wordRepository.save(new Word(null, "eloquent", "/ˈɛləkwənt/",
//                    "Fluent or persuasive in speaking or writing.",
//                    "Her eloquent speech moved the entire audience.",
//                    List.of("articulate", "expressive", "fluent")));
//        }
//    }
//}