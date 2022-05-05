package com.bsuir.sirius.controller;

import com.bsuir.sirius.service.UserService;
import com.bsuir.sirius.to.request.EditProfileUserDataTO;
import com.bsuir.sirius.to.request.NewImageRequestTO;
import com.bsuir.sirius.to.request.RegisterUserRequestTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
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

    @GetMapping(value = "/profile/edit")
    public String getProfileEditPage(Model model, Principal principal) {
        model.addAttribute("profileData", userService.getProfileData(principal.getName()));
        model.addAttribute("newProfileData", new EditProfileUserDataTO());
        return "profile-edit";
    }

    @PostMapping(value = "/profile/edit/save")
    public String setNewInfo(@ModelAttribute("newProfileData") EditProfileUserDataTO editProfileUserDataTO, Principal principal) {
        userService.setUserData(editProfileUserDataTO, principal.getName());
        return "redirect:/profile";
    }

    @PostMapping("/profile/image/update")
    public String updateImage(@RequestParam("image") MultipartFile file, Principal principal) throws IOException {
        userService.uploadImage(file, principal.getName());
        return "redirect:/profile";
    }

    @GetMapping("/my/images")
    public String getMyImages(Model model, Principal principal) {
        model.addAttribute("imageData", userService.getImages(principal.getName()));
        return "myImages";
    }

    @PostMapping(value = "/my/images/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String uploadMyImage(@ModelAttribute NewImageRequestTO newImageRequest, Principal principal) throws IOException {
        userService.uploadNewImage(newImageRequest, principal.getName());
        return "index";
    }

    @GetMapping("my/images/new")
    public String createNewImage(Model model) {
        model.addAttribute("newImageRequest", new NewImageRequestTO());
        return "newImage";
    }

    @GetMapping("my/wallet")
    public String getMyWallet(){
        return "myWallet";
    }

    @GetMapping("my/wallet/deposit")
    public String depositMyWallet(){
        return "deposit";
    }

    @GetMapping("my/wallet/withdraw")
    public String withdrawMyWallet(){
        return "withdraw";
    }

    @PostMapping("/service/wallet/{type}")
    public String walletActions(Principal principal, @PathVariable String type, @RequestParam BigDecimal amount){
        userService.walletAction(type, principal.getName(), amount);
        return "redirect:/my/wallet";
    }

}

