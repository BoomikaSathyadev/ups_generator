package com.kalai.ups.config;

import com.kalai.ups.entity.User;
import com.kalai.ups.repository.PendingUserRepository;
import com.kalai.ups.repository.UserRepository;
import com.kalai.ups.service.CustomOAuth2UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final PendingUserRepository pendingUserRepository;

    @Bean
    public CustomOAuth2UserService customOAuth2UserService() {
        return new CustomOAuth2UserService(userRepository, pendingUserRepository);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(auth -> auth
                .antMatchers("/login", "/access-denied", "/css/**", "/js/**").permitAll()
                .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .loginPage("/login")
                .userInfoEndpoint(u -> u.userService(customOAuth2UserService()))
                .successHandler(this::onSuccess)
                .failureHandler(this::onFailure)
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }

    private void onSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth) throws java.io.IOException {
        OAuth2User oauthUser = (OAuth2User) auth.getPrincipal();
        String email = oauthUser.getAttribute("email");
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            res.sendRedirect("/access-denied?reason=not_registered");
        } else if (user.getRole() == User.Role.ADMIN) {
            res.sendRedirect("/admin/dashboard");
        } else {
            res.sendRedirect("/app");
        }
    }

    private void onFailure(HttpServletRequest req, HttpServletResponse res, org.springframework.security.core.AuthenticationException ex) throws java.io.IOException {
        String reason = "not_registered";
        if (ex instanceof org.springframework.security.oauth2.core.OAuth2AuthenticationException) {
            reason = ((org.springframework.security.oauth2.core.OAuth2AuthenticationException) ex).getError().getErrorCode();
        }
        System.out.println(">>> FAILURE REASON: " + reason);
        if ("inactive".equals(reason)) {
            res.sendRedirect("/access-denied?reason=inactive");
        } else if ("pending".equals(reason)) {
            res.sendRedirect("/access-denied?reason=pending");
        } else {
            res.sendRedirect("/access-denied?reason=not_registered");
        }
    }
}
