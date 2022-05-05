package com.bsuir.sirius.to.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTO {
    private String username;
    private String email;
    private String wallet;
    private BigDecimal balance;
}
