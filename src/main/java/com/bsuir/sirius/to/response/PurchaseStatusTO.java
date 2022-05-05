package com.bsuir.sirius.to.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseStatusTO {
    private Boolean isDone;
    private String message;
}
