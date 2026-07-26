package com.kalai.ups.controller;

import com.kalai.ups.dto.UserDto;
import com.kalai.ups.entity.PendingUser;
import com.kalai.ups.entity.User;
import com.kalai.ups.repository.PendingUserRepository;
import com.kalai.ups.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final UserService userService;
    private final PendingUserRepository pendingUserRepository;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        model.addAttribute("adminName", principal.getAttribute("name"));
        model.addAttribute("adminPicture", principal.getAttribute("picture"));
        model.addAttribute("users", userService.findAll());
        model.addAttribute("pendingCount", pendingUserRepository.count());
        return "admin/dashboard";
    }

    @GetMapping("/requests")
    public String requests(@AuthenticationPrincipal OAuth2User principal, Model model) {
        model.addAttribute("adminName", principal.getAttribute("name"));
        model.addAttribute("adminPicture", principal.getAttribute("picture"));
        model.addAttribute("requests", pendingUserRepository.findAll());
        model.addAttribute("roles", User.Role.values());
        return "admin/requests";
    }

    @PostMapping("/requests/{id}/accept")
    public String acceptRequest(@PathVariable Long id, @RequestParam User.Role role) {
        PendingUser pending = pendingUserRepository.findById(id).orElseThrow();
        UserDto dto = new UserDto();
        dto.setEmail(pending.getEmail());
        dto.setName(pending.getName());
        dto.setRole(role);
        dto.setStatus(User.Status.ACTIVE);
        userService.save(dto);
        pendingUserRepository.deleteById(id);
        return "redirect:/admin/requests";
    }

    @PostMapping("/requests/{id}/deny")
    public String denyRequest(@PathVariable Long id) {
        pendingUserRepository.deleteById(id);
        return "redirect:/admin/requests";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("statuses", User.Status.values());
        return "admin/user-form";
    }

    @GetMapping("/users/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        model.addAttribute("userDto", dto);
        model.addAttribute("roles", User.Role.values());
        model.addAttribute("statuses", User.Status.values());
        return "admin/user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute UserDto userDto) {
        userService.save(userDto);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleStatus(@PathVariable Long id) {
        userService.toggleStatus(id);
        return "redirect:/admin/dashboard";
    }
}
