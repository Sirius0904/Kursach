package com.bsuir.sirius.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TRANSACTION_HISTORY")
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    private UserData buyer;

    @ManyToOne
    private UserData seller;
    @ManyToOne
    private Image image;
    private LocalDateTime transactionTime;
    private BigDecimal amount;
}
