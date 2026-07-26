package com.kalai.ups.service;

import com.kalai.ups.entity.PendingUser;
import com.kalai.ups.entity.User;
import com.kalai.ups.repository.PendingUserRepository;
import com.kalai.ups.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PendingUserRepository pendingUserRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(request);

        String email = oauthUser.getAttribute("email");
        System.out.println(">>> GOOGLE EMAIL: " + email);

        Optional<User> optUser = userRepository.findByEmail(email);

        if (optUser.isEmpty()) {
            // Save to pending if not already there
            if (!pendingUserRepository.existsByEmail(email)) {
                PendingUser pending = new PendingUser();
                pending.setEmail(email);
                pending.setName(oauthUser.getAttribute("name"));
                pending.setPictureUrl(oauthUser.getAttribute("picture"));
                pending.setGoogleId(oauthUser.getAttribute("sub"));
                pendingUserRepository.save(pending);
            }
            throw new OAuth2AuthenticationException(new OAuth2Error("pending", "pending", null));
        }

        User user = optUser.get();

        if (user.getStatus() == User.Status.INACTIVE) {
            throw new OAuth2AuthenticationException(new OAuth2Error("inactive", "inactive", null));
        }

        // Only sync Google profile if name hasn't been manually set by admin
        user.setGoogleId(oauthUser.getAttribute("sub"));
        if (user.getName() == null || user.getName().equals(oauthUser.getAttribute("name"))) {
            user.setName(oauthUser.getAttribute("name"));
        }
        user.setPictureUrl(oauthUser.getAttribute("picture"));
        userRepository.save(user);

        String role = "ROLE_" + user.getRole().name();
        System.out.println(">>> RETURNING ROLE: " + role);
        return new DefaultOAuth2User(
            List.of(new SimpleGrantedAuthority(role)),
            oauthUser.getAttributes(),
            "email"
        );
    }
}
