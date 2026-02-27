package com.zentrald.controller;

import com.zentrald.crypto.CustomPasswordEncoder;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

/**
 * Handles GET requests and returns view templates.
 * Authentication / authorization logic lives in AuthController and SecurityConfig.
 */
@Controller
public class ViewController {

    private final MessageSource messageSource;
    private final PasswordEncoder passwordEncoder;

    public ViewController(MessageSource messageSource, PasswordEncoder passwordEncoder) {
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
    }

    /** Root URL: login page for guests, dashboard redirect for authenticated users. */
    @GetMapping("/")
    public String index(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        return "login";
    }

    /**
     * /login is kept for Spring Security's default redirect-on-access-denied behavior
     * and for error/logout query parameters coming from AuthController.
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error",  required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model,
            Locale locale) {

        if (error  != null) model.addAttribute("errorMsg",
                messageSource.getMessage("login.error.invalid", null, locale));
        if (logout != null) model.addAttribute("logoutMsg",
                messageSource.getMessage("login.success.logout", null, locale));

        return "login";
    }

    /** Dashboard — requires authentication (enforced by SecurityConfig). */
    @GetMapping("/home")
    public String homePage(Authentication authentication, Model model) {
        model.addAttribute("username", authentication.getName());

        // Expose the active encoding scheme to the dashboard view
        String encoderInfo = (passwordEncoder instanceof CustomPasswordEncoder cpe)
                ? cpe.getHashAlgorithm() + " + " + cpe.getEncoding()
                : passwordEncoder.getClass().getSimpleName();
        model.addAttribute("encoderInfo", encoderInfo);

        return "home";
    }
}
