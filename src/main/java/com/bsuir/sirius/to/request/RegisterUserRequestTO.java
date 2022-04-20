package com.bsuir.sirius.to.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequestTO {
    private String username;
    private String password;
    private String confirmation;
    private String email;
}
