package com.bsuir.sirius.repository;

import com.bsuir.sirius.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, String> {
}
