package com.example.demo.repository;

import com.example.demo.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Wallet entity
 */

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
}