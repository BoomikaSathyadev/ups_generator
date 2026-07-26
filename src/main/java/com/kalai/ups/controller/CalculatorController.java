package com.kalai.ups.controller;

import com.kalai.ups.dto.CalculatorResult;
import com.kalai.ups.entity.UpsConfig;
import com.kalai.ups.service.CalculatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/calculator")
@RequiredArgsConstructor
public class CalculatorController {

    private final CalculatorService calculatorService;

    // Returns active UPS capacities for the dropdown
    @GetMapping("/configs")
    public List<Map<String, Object>> getConfigs() {
        return calculatorService.getActiveConfigs().stream()
            .map(c -> Map.<String, Object>of("kva", c.getKva(), "label", c.getKva() + " kVA"))
            .collect(Collectors.toList());
    }

    // Returns available backup times for the dropdown
    @GetMapping("/backup-times")
    public List<Integer> getBackupTimes() {
        return calculatorService.getAvailableBackupTimes();
    }

    // Main calculation endpoint
    @GetMapping("/calculate")
    public CalculatorResult calculate(@RequestParam int kva, @RequestParam int backupMinutes) {
        return calculatorService.calculate(kva, backupMinutes);
    }
}
