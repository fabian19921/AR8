package com.laverman.STM1D.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register-page")
    public String showRegisterPage() {
        return "register";
    }

    @GetMapping("/home")
    public String showHomePage() {
        return "home";
    }

    @GetMapping("/trainingplan")
    public String showTrainingPlanPage() {
        return "trainingplan";
    }

    @GetMapping("/workouts")
    public String showWorkoutsPage() {
        return "workouts";
    }

    @GetMapping("/profiel")
    public String showProfielPage() {
        return "profiel";
    }

    @GetMapping("/leaderboard")
    public String showLeaderboardPage() {
        return "leaderboard";
    }

    @GetMapping("/contact")
    public String showContactPage() {
        return "contact";
    }

    @GetMapping("/personal-records")
    public String showPersonalRecordsPage() {
        return "personal-records";
    }

    @GetMapping("/wachtwoord-vergeten")
    public String showWachtwoordVergetenPage() {
        return "wachtwoord-vergeten";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage() {
        return "reset-password";
    }
}
