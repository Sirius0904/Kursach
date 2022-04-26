package com.bsuir.sirius.to.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileUserDataTO {
    private String username;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String city;
    private String country;
    private String cardNumber;
    private String cvv;
    private String expirationDate;
    private String cardholderName;
}
