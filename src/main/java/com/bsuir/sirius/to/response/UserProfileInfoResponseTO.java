package com.bsuir.sirius.to.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileInfoResponseTO {
    private String username;
    private String email;
    private String fio;
    private String place;

}
