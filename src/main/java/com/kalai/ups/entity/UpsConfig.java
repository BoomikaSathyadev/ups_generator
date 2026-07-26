package com.kalai.ups.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ups_configs")
@Getter @Setter @NoArgsConstructor
public class UpsConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // UPS capacity in kVA e.g. 10, 20, 30, 100
    @Column(name = "kva", nullable = false, unique = true)
    private Integer kva;

    // UPS price in INR
    @Column(name = "ups_price", nullable = false)
    private Double upsPrice;

    // Price per battery in INR
    @Column(name = "battery_price", nullable = false)
    private Double batteryPrice;

    // Load factor: actual load as fraction of rated kVA (typically 0.8)
    @Column(name = "load_factor", nullable = false)
    private Double loadFactor = 0.8;

    // Inverter efficiency (typically 0.95 - 0.96)
    @Column(name = "inverter_efficiency", nullable = false)
    private Double inverterEfficiency = 0.96;

    // Number of batteries per string (series batteries, determines voltage)
    @Column(name = "batteries_per_string", nullable = false)
    private Integer batteriesPerString;

    // Number of parallel strings
    @Column(name = "num_strings", nullable = false)
    private Integer numStrings = 1;

    // Default ECV to use for sizing (10.8, 10.5, or 10.2)
    @Column(name = "ecv", nullable = false)
    private Double ecv = 10.8;

    @Column(nullable = false)
    private Boolean active = true;
}
