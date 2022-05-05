package com.bsuir.sirius.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "WALLET")
public class Wallet {
    @Id
    private String id;
    private String walletName;
    private String currency = "ERC";
    private BigDecimal balance = BigDecimal.ZERO;
}
