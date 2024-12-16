package com.example.demo.repository;

import com.example.demo.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Asset entity
 */

@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {}