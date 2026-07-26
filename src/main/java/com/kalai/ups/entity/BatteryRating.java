package com.kalai.ups.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "battery_ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ecv", "battery_ah", "discharge_minutes"})
})
@Getter @Setter @NoArgsConstructor
public class BatteryRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // End Cell Voltage e.g. 10.8, 10.5, 10.2
    @Column(nullable = false)
    private Double ecv;

    // Battery AH size e.g. 26, 42, 65, 100, 120, 200
    @Column(name = "battery_ah", nullable = false)
    private Integer batteryAh;

    // Discharge time in minutes e.g. 10, 15, 20, 30, 45, 60, 90, 120...
    @Column(name = "discharge_minutes", nullable = false)
    private Integer dischargeMinutes;

    // Watts per battery at this discharge time (from Exide table)
    @Column(nullable = false)
    private Double wattsPerBattery;
}
