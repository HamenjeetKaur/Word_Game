package com.example.springwordguess;

import com.example.springwordguess.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public void updateScore(Long userId, int newScore) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setScore(newScore);
            userRepository.save(user);
        } else {

        }
    }
}