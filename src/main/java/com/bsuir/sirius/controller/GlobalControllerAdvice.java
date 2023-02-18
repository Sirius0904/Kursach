package com.bsuir.sirius.controller;

import com.bsuir.sirius.entity.User;
import com.bsuir.sirius.entity.Wallet;
import com.bsuir.sirius.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Objects;

@ControllerAdvice
@AllArgsConstructor
public class GlobalControllerAdvice {

    private final UserService userService;

    @ModelAttribute("username")
    public String populateUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        return principal.getName();
    }

    @ModelAttribute("wallet")
    public Wallet getWallet(Principal principal) {
        if (principal == null) {
            return null;
        }
        User currentUser = userService.getCurrentUser(principal.getName());
        if (Objects.isNull(currentUser)) {
            return null;
        }
        return currentUser.getUserData().getWallet();
    }
}