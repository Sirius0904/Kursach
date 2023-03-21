package com.bsuir.sirius.controller;

import com.bsuir.sirius.service.UserService;
import com.bsuir.sirius.to.mvc.request.*;
import com.bsuir.sirius.to.mvc.response.DisplayImageTO;
import com.bsuir.sirius.to.mvc.response.PurchaseStatusTO;
import com.bsuir.sirius.to.mvc.response.UserTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/analyze")
    public String analyze(){
        return "analyzer";
    }

    @GetMapping(value = "/profile")
    public String getProfilePage(Principal principal, Model model) {
        model.addAttribute("lastFour", userService.getImages(principal.getName()).stream().limit(4).collect(Collectors.toList()));
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
    public String uploadMyImage(@ModelAttribute NewImageRequestTO newImageRequest, Principal principal, Model model) throws IOException {
        if (newImageRequest == null) {
            return "redirect:/my/images";
        }
        model.addAttribute("image", userService.uploadNewImage(newImageRequest, principal.getName()));
        return "uploadConfirmed";
    }

    @GetMapping("my/images/new")
    public String createNewImage(Model model) {
        model.addAttribute("newImageRequest", new NewImageRequestTO());
        return "newImage";
    }

    @GetMapping("my/wallet")
    public String getMyWallet(Model model, Principal principal) {
        model.addAttribute("transactions", userService.getLastTransactions(principal.getName()));
        model.addAttribute("transactionCountTotal", userService.getTransactionCount(principal.getName()));
        return "myWallet";
    }

    @GetMapping("my/wallet/deposit")
    public String depositMyWallet(DepositFormTO depositForm, Model model) {
        model.addAttribute("depositForm", depositForm);
        return "deposit";
    }

    @GetMapping("my/wallet/withdraw")
    public String withdrawMyWallet() {
        return "withdraw";
    }

    @PostMapping("/service/wallet/deposit")
    public String walletActions(@Valid @ModelAttribute("depositForm") DepositFormTO depositForm, BindingResult bindingResult, Principal principal) {

        if (bindingResult.getFieldError("date") == null) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth ym = YearMonth.parse(depositForm.getDate(), fmt);
            LocalDate dt = ym.atDay(1);
            if (dt.getYear() < LocalDate.now().getYear()) {
                bindingResult.rejectValue("date", "error.user", "Invalid expiration date!");
            }
        }
        if (bindingResult.hasErrors()) {
            return "deposit";
        }
        userService.walletAction("deposit", principal.getName(), depositForm, null);
        return "redirect:/my/wallet";
    }

    @PostMapping("/service/wallet/withdraw")
    public String walletActions(Principal principal, @RequestParam BigDecimal amount) {
        userService.walletAction("withdraw", principal.getName(), null, amount);
        return "redirect:/my/wallet";
    }

    @GetMapping("/gallery/image/{id}")
    public String getImageDetailedInfo(@PathVariable Integer id, Model model, Principal principal) {
        DisplayImageTO image = userService.getImageById(id, principal.getName());
        model.addAttribute("image", image);
        model.addAttribute("userData", userService.getUserInfo(image.getAuthor()));
        return "imageDetail";
    }


    @GetMapping("/gallery/store/buy/{id}")
    public String getBuyImagePage(@PathVariable Integer id, Model model, Principal principal) {
        model.addAttribute("image", userService.getImageById(id, principal.getName()));
        model.addAttribute("product", userService.getImageById(id, principal.getName()));
        return "buyImagePage";
    }

    @PostMapping("/gallery/store/buy")
    public RedirectView buyImage(@ModelAttribute("product") DisplayImageTO image, Principal principal, RedirectAttributes attributes) throws Exception {
        attributes.addFlashAttribute("status", userService.buyImage(image.getId(), principal.getName()));
        attributes.addFlashAttribute("image", image);
        return new RedirectView("/gallery/store/total");
    }

    @GetMapping("/gallery/store/total")
    public ModelAndView purchaseSuccessful(@ModelAttribute("status") PurchaseStatusTO status, @ModelAttribute("image") DisplayImageTO image, ModelMap model) {
        if (image == null || status == null || status.getIsDone() == null) {
            return new ModelAndView("redirect:/");
        }
        model.addAttribute("status", status);
        model.addAttribute("image", image);
        return new ModelAndView("purchaseSuccessful", model);
    }

    @GetMapping("/gallery")
    public String getGallery(Model model) {
        model.addAttribute("images", userService.getImages(null));
        return "gallery";
    }

    @GetMapping("/image/edit/{id}")
    public String editImage(Model model, @PathVariable Integer id, Principal principal) {
        if (!userService.isOwner(principal.getName(), id)) {
            return "redirect:/gallery";
        }
        model.addAttribute("editImage", new EditImageParametersRequestTO());
        model.addAttribute("image", userService.getImageById(id, principal.getName()));
        return "editImagePage";
    }

    @PostMapping("/image/edit")
    public String saveEditedImage(@ModelAttribute("editImage") EditImageParametersRequestTO newImage, @ModelAttribute("image") DisplayImageTO image) {
        userService.changeImageParams(newImage);
        return "redirect:/gallery/image/" + newImage.getId();
    }

    @GetMapping("/admin/users")
    public String getAllUsers(Model model, Principal principal) {
        List<UserTO> allUsers = userService.getAllUsers();
        if (allUsers == null) {
            return "redirect:/";
        }
        model.addAttribute("users", allUsers);
        return "adminTable";
    }


    @GetMapping("/gallery/search")
    public String findImage(Model model, @RequestParam String name, Principal principal) {
        model.addAttribute("images", userService.findImage(name, principal == null ? null : principal.getName()));
        return "gallery";
    }

}

