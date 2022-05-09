package com.bsuir.sirius.service;

import com.bsuir.sirius.entity.TransactionHistory;
import com.bsuir.sirius.repository.*;
import com.bsuir.sirius.to.rest.response.UserTransactionListResponseTO;
import com.bsuir.sirius.to.rest.response.UserTransactionTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserDataRepository userRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;

    public UserTransactionListResponseTO getUserTransactions(String username) {
        List<TransactionHistory> history = new ArrayList<>();
        if (username == null) {
            history = transactionHistoryRepository.findAll();
        } else {
            Integer id = userRepository.getUserDataByBaseUserUsername(username).getId();
            history = transactionHistoryRepository.findAllByBuyerIdOrSellerId(id, id);
        }
        List<UserTransactionTO> transactions = new ArrayList<>();
        history.forEach(e -> {
                    transactions.add(new UserTransactionTO()
                            .setSource(e.getBuyer() == null ? "deposit" : e.getBuyer().getBaseUser().getUsername() + " " + e.getBuyer().getWallet().getId())
                            .setTarget(e.getSeller() == null ? "withdraw" : e.getSeller().getBaseUser().getUsername() + " " + e.getSeller().getWallet().getId())
                            .setAmount(e.getAmount())
                            .setImageId(e.getImage() == null ? null : e.getImage().getId())
                    );
                }
        );
        return new UserTransactionListResponseTO(transactions);
    }
}
