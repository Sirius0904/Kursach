package com.bsuir.sirius.to.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserTransactionTO {
    private String source;
    private String target;
    private Long imageId;
    private BigDecimal amount;
}
