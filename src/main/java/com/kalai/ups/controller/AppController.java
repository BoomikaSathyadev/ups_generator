package com.kalai.ups.controller;

import com.kalai.ups.entity.User;
import com.kalai.ups.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AppController {

    private final UserRepository userRepository;

    @GetMapping({"/", "/app", "/calculator", "/calculator/**"})
    public String app(@AuthenticationPrincipal OAuth2User principal, Model model) {
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email).orElse(null);
        model.addAttribute("userName", principal.getAttribute("name"));
        model.addAttribute("userPicture", principal.getAttribute("picture"));
        model.addAttribute("isAdmin", user != null && user.getRole() == User.Role.ADMIN);
        return "calculator";
    }
}
