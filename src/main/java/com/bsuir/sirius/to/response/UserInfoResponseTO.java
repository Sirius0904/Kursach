package com.bsuir.sirius.to.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResponseTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}
