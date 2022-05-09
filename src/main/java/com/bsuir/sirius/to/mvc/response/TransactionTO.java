package com.bsuir.sirius.to.mvc.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionTO {
    private String sellerWallet;
    private String buyerWallet;
    private BigDecimal amount;
    private LocalDateTime time;
}
