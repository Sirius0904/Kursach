package com.bsuir.sirius.controller;

import com.bsuir.sirius.service.UserService;
import com.bsuir.sirius.to.request.RegisterUserRequestTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class SiriusController {

    private final UserService userService;

    @GetMapping(value = "/")
    public String getIndexPage() {
        return "index";
    }

    @GetMapping(value = "/collection")
    public String getPathPage() {
        return "collection";
    }

    @GetMapping(value = "/profile")
    public String getProfilePage(Principal principal, Model model) {
        model.addAttribute("userData", userService.getUserInfo(principal.getName()));
        return "profile";
    }

    @GetMapping(value = "/pinkColl")
    public String getFirstPage() {
        return "pinkColl";
    }

    @GetMapping(value = "/darkColl")
    public String getSecondPage() {
        return "darkColl";
    }

    @GetMapping(value = "/girlColl")
    public String getThirdPage() {
        return "girlColl";
    }

    @GetMapping(value = "/registration")
    public String registration(Model model) {
        model.addAttribute("registrationTo", new RegisterUserRequestTO());
        return "registration";
    }

    @PostMapping(value = "/register")
    public String registerUser(@ModelAttribute RegisterUserRequestTO registerUserTO) throws Exception { //todo create custom validation
        userService.registerUser(registerUserTO);
        return "redirect:/";
    }
}

