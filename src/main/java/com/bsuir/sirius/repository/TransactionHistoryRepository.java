package com.bsuir.sirius.repository;

import com.bsuir.sirius.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Integer> {
    List<TransactionHistory> findAllByBuyerIdOrSellerId(Integer var1, Integer var2);
}
