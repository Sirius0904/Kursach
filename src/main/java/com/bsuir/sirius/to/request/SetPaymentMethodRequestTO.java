package com.bsuir.sirius.to.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetPaymentMethodRequestTO {
    private String wallet;
}
