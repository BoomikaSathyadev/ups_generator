package com.kalai.ups.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CalculatorResult {

    private int upsKva;
    private int backupMinutes;

    // Sizing outputs
    private double wattsPerBatteryRequired;
    private int recommendedBatteryAh;
    private double wattsPerBatteryProvided;
    private int batteriesPerString;
    private int numStrings;
    private int totalBatteries;

    // Pricing
    private double upsPrice;
    private double pricePerBattery;
    private double totalBatteryPrice;
    private double totalPrice;

    // Error message if sizing fails
    private String error;
}
