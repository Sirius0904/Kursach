package com.bsuir.sirius.service;

import com.bsuir.sirius.entity.*;
import com.bsuir.sirius.enumeration.ImageType;
import com.bsuir.sirius.repository.*;
import com.bsuir.sirius.to.request.EditProfileUserDataTO;
import com.bsuir.sirius.to.request.NewImageRequestTO;
import com.bsuir.sirius.to.request.RegisterUserRequestTO;
import com.bsuir.sirius.to.response.DisplayImageTO;
import com.bsuir.sirius.to.response.UserProfileInfoResponseTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserDataRepository userDataRepository;
    private final BCryptPasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final ImageRepository imageRepository;
    private final ImageCollectionRepository collectionRepository;
    private final ImageDataRepository imageDataRepository;
    private final WalletRepository walletRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;


    public User getCurrentUser(String username){
        return userRepository.getUserByUsername(username);
    }

    public void registerUser(RegisterUserRequestTO request) throws Exception {
        if (userRepository.getUserByUsername(request.getUsername()) != null) {
            throw new Exception("User already registered");
        }
        if (!request.getPassword().equals(request.getConfirmation())) {
            throw new Exception("Passwords dont match");
        }
        User user = new User();
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.getOne(1));
        user.setRoles(roles);
        user.setUsername(request.getUsername());
        UserData userData = new UserData();
        userData.setEmail(request.getEmail());
        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID().toString());
        walletRepository.saveAndFlush(wallet);
        userData.setWallet(wallet);
        userDataRepository.saveAndFlush(userData);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setUserData(userData);
        userRepository.saveAndFlush(user);
    }

    public UserProfileInfoResponseTO getUserInfo(String username) {
        User user = userRepository.getUserByUsername(username);
        UserProfileInfoResponseTO response = new UserProfileInfoResponseTO();

        if (user.getUserData() != null) {
            if (user.getUserData().getFirstName() != null) {
                response.setFio(user.getUserData().getFirstName());
            }
            if (user.getUserData().getLastName() != null && response.getFio() != null) {
                response.setFio(response.getFio() + " " + user.getUserData().getLastName());
            }
            response.setEmail(user.getUserData().getEmail());
            if (user.getUserData().getProfileImage() != null) {
                response.setProfileImage(user.getUserData().getProfileImage().getPath());
            }
        }
        if (user.getUserData() != null) {
            if (user.getUserData().getCountry() != null && user.getUserData().getCountry().length() != 0) {
                response.setPlace(user.getUserData().getCountry());
            }
            if (user.getUserData().getCity() != null && user.getUserData().getCity().length() != 0) {
                if (response.getPlace() == null || response.getPlace().length() == 0) {
                    response.setPlace(user.getUserData().getCity());
                } else {
                    response.setPlace(response.getPlace() + ", " + user.getUserData().getCity());
                }
            }
        }
        response.setUsername(user.getUsername());
        return response;
    }

    public EditProfileUserDataTO getProfileData(String name) {
        User user = userRepository.getUserByUsername(name);
        EditProfileUserDataTO response = new EditProfileUserDataTO();
        if (user.getUserData() != null) {
            response.setFirstName(user.getUserData().getFirstName());
            response.setLastName(user.getUserData().getLastName());
            response.setPhoneNumber(user.getUserData().getPhoneNumber());
            response.setEmail(user.getUserData().getEmail());
            if(user.getUserData().getProfileImage() != null){
                response.setProfileImage("\\" + user.getUserData().getProfileImage().getPath());
            }
        }
        response.setUsername(user.getUsername());
        return response;
    }

    public void setUserData(EditProfileUserDataTO request, String username) {
        UserData userData = userRepository.getUserByUsername(username).getUserData();
        if (request.getEmail() != null && request.getEmail().length() != 0) {
            userData.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null && request.getFirstName().length() != 0 && checkSpecialSymbols(request.getFirstName())) {
            userData.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && request.getLastName().length() != 0 && checkSpecialSymbols(request.getLastName())) {
            userData.setLastName(request.getLastName());
        }
        if (request.getCity() != null && checkSpecialSymbols(request.getCity())) {
            userData.setCity(request.getCity());
        }
        if (request.getCountry() != null && checkSpecialSymbols(request.getCountry())) {
            userData.setCountry(request.getCountry());
        }
        userDataRepository.saveAndFlush(userData);
    }

    private boolean checkSpecialSymbols(String s) {
        if (s.length() >= 16) {
            return false;
        }
        Pattern p = Pattern.compile("[^A-Za-z]");
        Matcher m = p.matcher(s);
        return !m.find();
    }


    public void createCollection(String collectionName, String username) {

    }

    public void addToCollection(String username, String collectionId, String productId) {

    }

    public void removeFromCollection(String username, String collectionId, String productId) {

    }

    public void uploadImage(MultipartFile file, String username) throws IOException {
        System.out.println(file.getContentType());
        if (file.isEmpty()) {
            return;
        }
        if(!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")){
            return;
        }
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uploadDir = "user-photos/" + userRepository.getUserByUsername(username).getId();

        ImageData imageData = new ImageData();

        Image image = new Image();
        image.setImageName("profile_image");
        image.setOwner(userRepository.getUserByUsername(username).getUserData());
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            imageData.setPath(filePath.toString());
            imageData.setImageType(ImageType.PROFILE);
            imageDataRepository.saveAndFlush(imageData);
            image.setImageData(imageData);
            imageRepository.saveAndFlush(image);
            UserData userData = userRepository.getUserByUsername(username).getUserData();
            userData.setProfileImage(imageData);
            userDataRepository.saveAndFlush(userData);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }

    }

    public void uploadNewImage(NewImageRequestTO request, String username) throws IOException {
        if (request == null || request.getImage().isEmpty()) {
            return;
        }
        if(!request.getImage().getContentType().equals("image/jpeg") && !request.getImage().getContentType().equals("image/png")){
            return;
        }
        String fileName = StringUtils.cleanPath(request.getImage().getOriginalFilename());
        User user = userRepository.getUserByUsername(username);
        String uploadDir = "user-photos/" + user.getId();

        ImageData imageData = new ImageData();

        Image image = new Image();
        image.setImageName(request.getName());
        image.setDescription(request.getDescription());
        image.setOwner(user.getUserData());
        image.setIsSellable(request.getStatus().getValue());
        image.setPrice(request.getPrice());
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = request.getImage().getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            imageData.setPath(filePath.toString());
            imageData.setImageType(ImageType.PUBLIC);
            imageDataRepository.saveAndFlush(imageData);
            image.setImageData(imageData);
            imageRepository.saveAndFlush(image);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
    }

    public List<DisplayImageTO> getImages(String username) {
        List<DisplayImageTO> images = new ArrayList<>();
        if (username == null) {
            imageRepository.findAll().stream().filter(e -> e.getImageData().getImageType().equals(ImageType.PUBLIC)).forEach(image -> {
                images.add(new DisplayImageTO(
                        "\\" + image.getImageData().getPath(),
                        image.getImageName(),
                        "\\" + image.getOwner().getProfileImage().getPath(),
                        image.getDescription(),
                        image.getOwner().getFirstName() + image.getOwner().getLastName(),
                        image.getLikeCount(),
                        image.getPrice(),
                        image.getIsSellable()
                ));
            });
        } else {
            User user = userRepository.getUserByUsername(username);
            user.getUserData().getImages().stream().filter(e -> e.getImageData().getImageType().equals(ImageType.PUBLIC)).forEach(image -> {
                images.add(new DisplayImageTO(
                        "\\" + image.getImageData().getPath(),
                        image.getImageName(),
                        "\\" + user.getUserData().getProfileImage().getPath(),
                        image.getDescription(),
                        user.getUsername(),
                        image.getLikeCount(),
                        image.getPrice(),
                        image.getIsSellable()
                ));
            });
        }
        return images;
    }

    public void like(String type, String id, String username) {

    }

    public void removeProduct(String type, String collectionId, String username) {

    }

    public void sellProduct(String type, String productId, BigDecimal price, String username) {

    }

    public void changeSellableStatus(String type, String productId, String username) {

    }

    public void buyProduct(String type, String productId, String username) {

    }

    public List<TransactionHistory> getUserTransactions(String username) {
        return null;
    }

    public void doTransfer(String username, String recipient, BigDecimal amount) {

    }

    @Transactional
    public void walletAction(String type, String username, BigDecimal amount) {
        User user = userRepository.getUserByUsername(username);
        TransactionHistory transaction = new TransactionHistory();
        transaction.setTransactionTime(LocalDateTime.now());
        Wallet wallet = user.getUserData().getWallet();
        switch (type){
            case "deposit":
                transaction.setBuyer(user.getUserData());
                transaction.setAmount(amount);
                wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
                walletRepository.saveAndFlush(wallet);
                transactionHistoryRepository.saveAndFlush(transaction);
                break;
            case "withdraw":
                transaction.setSeller(user.getUserData());
                transaction.setAmount(amount.multiply(BigDecimal.valueOf(-1)));
                wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
                walletRepository.saveAndFlush(wallet);
                transactionHistoryRepository.saveAndFlush(transaction);
        }
    }

}
