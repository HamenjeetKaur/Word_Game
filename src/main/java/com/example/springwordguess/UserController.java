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


    @GetMapping("/home")
    public String showHomePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            return "home"; // Assuming you have a "home.html" template
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/panel")
    public String showPanel(HttpSession session) {
        // Check if the admin is logged in
        if (session.getAttribute("isAdmin") != null && (boolean) session.getAttribute("isAdmin")) {
            return "panel"; // Assuming you have a panel.html template
        } else {
            // Redirect to login if not logged in or not admin
            return "redirect:/login";
        }
    }


    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes) {
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("emailError", "Email address is required");
            return "redirect:/login";
        }

        if (password == null || password.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("passwordError", "Password is required");
            return "redirect:/login";
        }

        // Check if the credentials are for the admin
        if ("admin@gmail.com".equals(email) && "admin1234".equals(password)) {
            session.setAttribute("isAdmin", true);
            return "redirect:/panel"; // Redirect to the panel for admins
        }

        // If not admin, check the database for a regular user
        User user = userRepository.findByEmail(email);

        if (user != null) {
            if (checkPassword(password, user.getPassword())) {
                session.setAttribute("user", user);
                return "redirect:/game"; // Redirect to the game page for regular users
            } else {
                redirectAttributes.addFlashAttribute("passwordError", "Wrong password");
            }
        } else {
            redirectAttributes.addFlashAttribute("emailError", "Email not found");
        }

        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {

        return "dashboard";
    }




    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
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

        // Check if the email is already registered
        if (userRepository.findByEmail(user.getEmail()) != null) {
            redirectAttributes.addFlashAttribute("emailError", "Email is already registered");
            return "redirect:/signup";
        }


        user.setPassword(user.getPassword());

        // Save the user to the database
        userRepository.save(user);

        // Redirect to login page after successful signup
        return "redirect:/login";
    }
}