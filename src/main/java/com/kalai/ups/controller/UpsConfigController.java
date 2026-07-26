package com.kalai.ups.controller;

import com.kalai.ups.entity.UpsConfig;
import com.kalai.ups.repository.PendingUserRepository;
import com.kalai.ups.repository.UpsConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/ups-configs")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class UpsConfigController {

    private final UpsConfigRepository upsConfigRepository;
    private final PendingUserRepository pendingUserRepository;

    @GetMapping
    public String list(@AuthenticationPrincipal OAuth2User principal, Model model) {
        model.addAttribute("adminName", principal.getAttribute("name"));
        model.addAttribute("adminPicture", principal.getAttribute("picture"));
        model.addAttribute("pendingCount", pendingUserRepository.count());
        model.addAttribute("configs", upsConfigRepository.findAll());
        return "admin/ups-configs";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        UpsConfig cfg = new UpsConfig();
        cfg.setLoadFactor(0.8);
        cfg.setInverterEfficiency(0.96);
        cfg.setNumStrings(1);
        cfg.setEcv(10.8);
        cfg.setActive(true);
        model.addAttribute("cfg", cfg);
        return "admin/ups-config-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("cfg", upsConfigRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Config not found: " + id)));
        return "admin/ups-config-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute UpsConfig cfg) {
        upsConfigRepository.save(cfg);
        return "redirect:/admin/ups-configs";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        upsConfigRepository.deleteById(id);
        return "redirect:/admin/ups-configs";
    }
}
