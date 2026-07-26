package com.kalai.ups.service;

import com.kalai.ups.dto.CalculatorResult;
import com.kalai.ups.entity.BatteryRating;
import com.kalai.ups.entity.UpsConfig;
import com.kalai.ups.repository.BatteryRatingRepository;
import com.kalai.ups.repository.UpsConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final UpsConfigRepository upsConfigRepository;
    private final BatteryRatingRepository batteryRatingRepository;

    /**
     * Core sizing logic — mirrors the Excel sheet exactly:
     *
     * 1. Actual Load (KW)  = UPS kVA × loadFactor × 0.8 (pf assumed 0.8)
     * 2. Watts/Battery     = (ActualLoad × 1000) ÷ inverterEfficiency ÷ batteriesPerString
     * 3. Lookup Exide table at (ecv, backupMinutes) → find smallest AH whose
     *    wattsPerBattery >= required watts/battery
     * 4. Total batteries   = batteriesPerString × numStrings
     */
    public CalculatorResult calculate(int kva, int backupMinutes) {
        UpsConfig cfg = upsConfigRepository.findByKva(kva).orElse(null);
        if (cfg == null) {
            CalculatorResult err = new CalculatorResult();
            err.setError("No configuration found for " + kva + " kVA. Please contact admin.");
            return err;
        }

        // Step 1: Actual load in KW (load factor × power factor 0.8)
        double actualLoadKw = kva * cfg.getLoadFactor() * 0.8;

        // Step 2: Watts required per battery per string
        double wattsRequired = (actualLoadKw * 1000.0) / cfg.getInverterEfficiency() / cfg.getBatteriesPerString();

        // Step 3: Lookup Exide table — find smallest AH that can deliver >= wattsRequired
        List<BatteryRating> ratings = batteryRatingRepository
            .findByEcvAndDischargeMinutesOrderByBatteryAhAsc(cfg.getEcv(), backupMinutes);

        if (ratings.isEmpty()) {
            CalculatorResult err = new CalculatorResult();
            err.setError("No battery discharge data found for " + backupMinutes + " min backup at ECV " + cfg.getEcv() + "V.");
            return err;
        }

        BatteryRating selected = null;
        for (BatteryRating r : ratings) {
            if (r.getWattsPerBattery() >= wattsRequired) {
                selected = r;
                break;
            }
        }

        if (selected == null) {
            // All batteries are undersized — pick the largest available and flag it
            selected = ratings.get(ratings.size() - 1);
        }

        // Step 4: Total batteries
        int totalBatteries = cfg.getBatteriesPerString() * cfg.getNumStrings();

        // Pricing
        double batteryTotal = totalBatteries * cfg.getBatteryPrice();
        double total = cfg.getUpsPrice() + batteryTotal;

        return new CalculatorResult(
            kva,
            backupMinutes,
            Math.round(wattsRequired * 100.0) / 100.0,
            selected.getBatteryAh(),
            selected.getWattsPerBattery(),
            cfg.getBatteriesPerString(),
            cfg.getNumStrings(),
            totalBatteries,
            cfg.getUpsPrice(),
            cfg.getBatteryPrice(),
            batteryTotal,
            total,
            null
        );
    }

    public List<UpsConfig> getActiveConfigs() {
        return upsConfigRepository.findByActiveTrueOrderByKvaAsc();
    }

    public List<Integer> getAvailableBackupTimes() {
        return batteryRatingRepository.findDistinctDischargeMinutes();
    }
}
