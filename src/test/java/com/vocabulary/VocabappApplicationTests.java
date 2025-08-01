package com.vocabulary;

import com.vocabulary.model.Word;
import com.vocabulary.repository.WordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VocabularyApplicationTests {

	@Autowired
	private WordRepository wordRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void testCreateWord() {
		Word word = new Word();
		word.setTerm("test");
		word.setDefinition("test definition");
		wordRepository.save(word);

		Optional<Word> foundWord = wordRepository.findByTerm("test");
		assertThat(foundWord).isPresent();
		assertThat(foundWord.get().getDefinition()).isEqualTo("test definition");
	}
}