package com.zentrald.controller;

import com.zentrald.service.AccountService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

@Controller
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;
    private final MessageSource messageSource;

    public AccountController(AccountService accountService, MessageSource messageSource) {
        this.accountService = accountService;
        this.messageSource = messageSource;
    }

    // ── Find Username ──────────────────────────────────────────────────────────

    @GetMapping("/find-username")
    public String findUsernamePage() {
        return "find-username";
    }

    @PostMapping("/find-username")
    public String findUsername(
            @RequestParam String name,
            @RequestParam String email,
            Model model,
            Locale locale) {

        accountService.findUsernameByNameAndEmail(name, email).ifPresentOrElse(
                username -> model.addAttribute("foundUsername", username),
                () -> model.addAttribute("errorMsg",
                        messageSource.getMessage("find.error.notfound", null, locale))
        );
        return "find-username";
    }

    // ── Reset Password ─────────────────────────────────────────────────────────

    @GetMapping("/reset-password")
    public String resetPasswordPage() {
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String username,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Model model,
            Locale locale) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("errorMsg",
                    messageSource.getMessage("reset.error.mismatch", null, locale));
            return "reset-password";
        }
        if (newPassword.length() < 4) {
            model.addAttribute("errorMsg",
                    messageSource.getMessage("reset.error.tooshort", null, locale));
            return "reset-password";
        }
        if (!accountService.existsByUsername(username)) {
            model.addAttribute("errorMsg",
                    messageSource.getMessage("reset.error.notfound", null, locale));
            return "reset-password";
        }

        accountService.resetPassword(username, newPassword);
        model.addAttribute("successMsg",
                messageSource.getMessage("reset.success", null, locale));
        return "reset-password";
    }
}
