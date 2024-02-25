package com.example.springwordguess;
import com.example.springwordguess.WordRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class WordController {
    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private UserService userService;


    @PostMapping("/game")
    public String getWord(@ModelAttribute("selectedLevel") String selectedLevel, Model model, HttpSession session) {
        Word word = wordRepository.findRandomWordByLevel(selectedLevel);
        model.addAttribute("word", word);
        session.setAttribute("word", word);
        session.setAttribute("chancesLeft", 5);
        return "redirect:/showWord";
    }

    @GetMapping("/showWord")
    public String showWord(HttpSession session, Model model) {
        Word wordarray = (Word) session.getAttribute("word");
        model.addAttribute("GivenHints", wordarray.getHint());
        return "wordInput";
    }

    @GetMapping("/panel/words")
    public String showWords(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getEmail().equals("admin@gmail.com") && user.getPassword().equals("admin123")) {
            List<Word> words = wordRepository.findAll();
            model.addAttribute("words", words);
            return "panel";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/panel/addWord")
    public String addWord(@RequestParam String word, @RequestParam String hint, @RequestParam String level, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getEmail().equals("admin@gmail.com") && user.getPassword().equals("admin123")) {
            Word newWord = new Word();
            newWord.setWord(word);
            newWord.setHint(hint);
            newWord.setLevel(level);
            wordRepository.save(newWord);
        } else {
            return "redirect:/login";
        }
        return "redirect:/panel/words";
    }

    @PostMapping("/panel/updateWord")
    public String updateWord(@RequestParam Long updateWordId, @RequestParam String newWord, @RequestParam String newHint, @RequestParam String newLevel, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getEmail().equals("admin@gmail.com") && user.getPassword().equals("admin123")) {
            Word existingWord = wordRepository.findById(updateWordId).orElse(null);
            if (existingWord != null) {
                existingWord.setWord(newWord);
                existingWord.setHint(newHint);
                existingWord.setLevel(newLevel);
                wordRepository.save(existingWord);
            }
        } else {
            return "redirect:/login";
        }
        return "redirect:/panel/words";
    }

    @PostMapping("/panel/deleteWord")
    public String deleteWord(@RequestParam Long deleteWordId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getEmail().equals("admin@gmail.com") && user.getPassword().equals("admin123")) {
            wordRepository.deleteById(deleteWordId);
        } else {
            return "redirect:/login";
        }
        return "redirect:/panel/words";
    }


    @PostMapping("/getWord")
    public String login(@RequestParam String word, HttpSession session, Model model) {
        Word wordarray = (Word) session.getAttribute("word");
        int chancesLeft = (int) session.getAttribute("chancesLeft");
        model.addAttribute("GivenHints", wordarray.getHint());
        if (word != null && wordarray.getWord().equalsIgnoreCase(word)) {
            model.addAttribute("message", "Congratulations! You win. Check your Score at HomePage.");
            session.removeAttribute("chancesLeft");
            User user = (User) session.getAttribute("user");

            if (user != null) {
                int currentScore = user.getScore();
                int newScore = currentScore + 1;
                user.setScore(newScore);
                userService.updateScore(user.getId(), newScore);
            }
            return "wordInput";
        } else {
            chancesLeft--;
            if (chancesLeft == 0) {
                model.addAttribute("message", "Sorry! You Lose. Check your Score at HomePage.");
                session.removeAttribute("chancesLeft");
                return "wordInput";
            } else {
                model.addAttribute("mess", "Please Try again. Chances left: " + chancesLeft);
                session.setAttribute("chancesLeft", chancesLeft);
            }
            return "wordInput";
        }
    }

}
