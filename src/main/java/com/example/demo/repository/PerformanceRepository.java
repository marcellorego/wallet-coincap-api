package com.example.demo.repository;

import com.example.demo.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Wallet Performance entity
 */

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Performance.PerformanceId> {
}