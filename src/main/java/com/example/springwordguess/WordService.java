package com.example.springwordguess;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    public List<Word> getAllWords() {

        return wordRepository.findAll();
    }

    public Optional<Word> getWordById(Long id) {

        return wordRepository.findById(id);
    }

    public void addWord(Word word) {

        wordRepository.save(word);
    }

    public void updateWord(Word updatedWord) {

        wordRepository.save(updatedWord);
    }

    public void deleteWord(Long id) {

        wordRepository.deleteById(id);
    }
}