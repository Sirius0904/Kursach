package com.bsuir.sirius.to.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTransactionListResponseTO {
    private List<UserTransactionTO> transactions;
}
