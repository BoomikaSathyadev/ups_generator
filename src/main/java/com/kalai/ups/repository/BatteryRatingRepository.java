package com.kalai.ups.repository;

import com.kalai.ups.entity.BatteryRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BatteryRatingRepository extends JpaRepository<BatteryRating, Long> {

    // Find all ratings for a given ECV and discharge time, ordered by AH ascending
    List<BatteryRating> findByEcvAndDischargeMinutesOrderByBatteryAhAsc(Double ecv, Integer dischargeMinutes);

    // Find exact rating for a specific battery AH at given ECV and discharge time
    Optional<BatteryRating> findByEcvAndBatteryAhAndDischargeMinutes(Double ecv, Integer batteryAh, Integer dischargeMinutes);

    // Check if table is already seeded
    boolean existsByEcv(Double ecv);

    // Get all distinct discharge times available
    @Query("SELECT DISTINCT b.dischargeMinutes FROM BatteryRating b ORDER BY b.dischargeMinutes")
    List<Integer> findDistinctDischargeMinutes();

    // Get all distinct AH sizes available for a given ECV
    @Query("SELECT DISTINCT b.batteryAh FROM BatteryRating b WHERE b.ecv = :ecv ORDER BY b.batteryAh")
    List<Integer> findDistinctAhByEcv(@Param("ecv") Double ecv);
}
