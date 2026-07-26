package com.kalai.ups.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied(@RequestParam(required = false) String reason, Model model) {
        if ("inactive".equals(reason)) {
            model.addAttribute("message", "Your account is currently inactive. Please contact your administrator.");
            model.addAttribute("pending", false);
        } else if ("pending".equals(reason)) {
            model.addAttribute("message", "Your access request has been sent to the administrator. You'll be able to login once approved.");
            model.addAttribute("pending", true);
        } else {
            model.addAttribute("message", "Your account is not authorized to access this application. Please contact your administrator.");
            model.addAttribute("pending", false);
        }
        return "access-denied";
    }
}
