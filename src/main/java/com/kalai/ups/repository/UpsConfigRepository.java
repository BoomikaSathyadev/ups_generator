package com.kalai.ups.repository;

import com.kalai.ups.entity.UpsConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UpsConfigRepository extends JpaRepository<UpsConfig, Long> {

    List<UpsConfig> findByActiveTrueOrderByKvaAsc();

    Optional<UpsConfig> findByKva(Integer kva);
}
