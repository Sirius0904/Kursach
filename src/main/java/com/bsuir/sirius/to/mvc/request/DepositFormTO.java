package com.bsuir.sirius.to.mvc.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositFormTO {
    @Pattern(regexp = "(^[0-9]{4} [0-9]{4} [0-9]{4} [0-9]{4}$)", message = "Check your card number")
    private String cardNumber;

    @Pattern(regexp = "(^[0-9]{2}\\/[0-9]{2}$)", message = "Expiration date is not valid!")
    private String date;

    @Pattern(regexp = "(^[0-9]{3}$)", message = "Check your CVV code!")
    private String cvv;

    @Pattern(regexp = "(^[aA-zZ]* [aA-zZ]*$)", message = "Credentials are not valid!")
    @NotNull
    private String name;

    @NotNull
    private BigDecimal amount;
}
