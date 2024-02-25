package com.example.springwordguess;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    private final UserRepository userRepository;
    @Autowired
    public UserController(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "index";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @GetMapping("/game")
    public String wordForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        } else {
            return "redirect:/login";
        }

        model.addAttribute("levels", new String[]{"Easy", "Medium", "Hard"});
        model.addAttribute("selectedLevel", "");

        return "wordForm";
    }
    @GetMapping("/panel")
    public String showPanel(HttpSession session) {
        // Check if the user is logged in
        User user = (User) session.getAttribute("user");
        if (user != null && user.getEmail().equals("admin@gmail.com") && user.getPassword().equals("admin123")) {
            return "redirect:/panel/words";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("emailError", "Email address is required");
            return "redirect:/login";
        }

        if (password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("passwordError", "Password is required");
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            return "redirect:/game";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "index";
        }
    }


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }


    private boolean checkPassword(String plainPassword, String hashedPasswordFromDatabase) {

        return plainPassword.equals(hashedPasswordFromDatabase);
    }


    @PostMapping("/signup")
    public String signup(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
        // Validate input
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("emailError", "Email address is required");
            return "redirect:/signup";
        }

        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("passwordError", "Password is required");
            return "redirect:/signup";
        }


        if (userRepository.findByEmail(user.getEmail()) != null) {
            redirectAttributes.addFlashAttribute("emailError", "Email is already registered");
            return "redirect:/signup";
        }

        user.setPassword(user.getPassword());

        userRepository.save(user);

        return "redirect:/login";
    }
}