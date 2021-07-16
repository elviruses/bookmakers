package org.example.bookmaker.repo;

import org.example.bookmaker.entity.Zenit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ZenitRepository extends JpaRepository<Zenit, Long> {
    @Query("select z from Zenit z where z.id = :forecastId and z.isDeleted = false")
    Optional<Forecast> findForecastById(@Param("forecastId") Long id);

}
