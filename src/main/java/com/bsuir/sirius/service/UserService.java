package com.bsuir.sirius.service;

import com.bsuir.sirius.entity.*;
import com.bsuir.sirius.enumeration.ImageType;
import com.bsuir.sirius.repository.*;
import com.bsuir.sirius.to.request.EditImageParametersRequestTO;
import com.bsuir.sirius.to.request.EditProfileUserDataTO;
import com.bsuir.sirius.to.request.NewImageRequestTO;
import com.bsuir.sirius.to.request.RegisterUserRequestTO;
import com.bsuir.sirius.to.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
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
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserDataRepository userDataRepository;
    private final BCryptPasswordEncoder encoder;
    private final RoleRepository roleRepository;
    private final ImageRepository imageRepository;
    private final ImageDataRepository imageDataRepository;
    private final WalletRepository walletRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;


    public User getCurrentUser(String username) {
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
        ImageData imageData = new ImageData().setImageType(ImageType.PROFILE).setPath("user-photos\\1\\unknown.png");
        imageDataRepository.saveAndFlush(imageData);
        userData.setProfileImage(imageData);
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
            if (user.getUserData().getProfileImage() != null) {
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
        if (!request.getCity().isEmpty() && !request.getCity().isBlank() && request.getCity() != null && checkSpecialSymbols(request.getCity())) {
            userData.setCity(request.getCity());
        }
        if (request.getCountry() != null && !request.getCountry().isBlank() && !request.getCountry().isEmpty() && checkSpecialSymbols(request.getCountry())) {
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


    public List<TransactionTO> getLastTransactions(String username) {
        List<TransactionTO> transactions = new ArrayList<>();
        User user = userRepository.getUserByUsername(username);
        Integer id = user.getUserData().getId();
        List<TransactionHistory> transactionHistory = transactionHistoryRepository.findAllByBuyerIdOrSellerId(id, id);
        transactionHistory.stream()
                .sorted(Comparator.comparing(TransactionHistory::getTransactionTime).reversed())
                .limit(7)
                .collect(Collectors.toList())
                .forEach(e -> {
                    transactions.add(
                            new TransactionTO(
                                    e.getSeller() == null ? null : e.getSeller().getWallet().getId(),
                                    e.getBuyer() == null ? null : e.getBuyer().getWallet().getId(),
                                    e.getAmount(),
                                    e.getTransactionTime()
                            )
                    );
                });
        return transactions;
    }

    public Integer getTransactionCount(String username) {
        User user = userRepository.getUserByUsername(username);
        Integer id = user.getUserData().getId();
        return transactionHistoryRepository.findAllByBuyerIdOrSellerId(id, id).size();
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
        if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
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

    public DisplayImageTO uploadNewImage(NewImageRequestTO request, String username) throws IOException {
        if (request == null || request.getImage().isEmpty()) {
            return null;
        }
        if (!request.getImage().getContentType().equals("image/jpeg") && !request.getImage().getContentType().equals("image/png")) {
            return null;
        }
        String fileName = StringUtils.cleanPath(request.getImage().getOriginalFilename());
        User user = userRepository.getUserByUsername(username);
        String uploadDir = "user-photos/" + user.getId();
        Image savedImage = new Image();
        ImageData imageData = new ImageData();

        Image image = new Image();
        image.setImageName(request.getName() == null ? request.getImage().getName() : request.getName());
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
            savedImage = imageRepository.saveAndFlush(image);
        } catch (IOException ioe) {
            throw new IOException("Could not save image file: " + fileName, ioe);
        }
        DisplayImageTO imageTO = new DisplayImageTO();
        imageTO.setName(savedImage.getImageName());
        return imageTO;
    }

    public List<DisplayImageTO> getImages(String username) {
        List<DisplayImageTO> images = new ArrayList<>();
        if (username == null) {
            imageRepository.findAll().stream().filter(e -> e.getImageData().getImageType().equals(ImageType.PUBLIC)).forEach(image -> {
                images.add(new DisplayImageTO(

                        image.getId().toString(),
                        "\\" + image.getImageData().getPath(),
                        image.getImageName(),
                        image.getOwner().getProfileImage() == null ? "" : "\\" + image.getOwner().getProfileImage().getPath(),
                        image.getDescription(),
                        image.getOwner().getFirstName() + " " + image.getOwner().getLastName(),
                        image.getLikeCount(),
                        image.getPrice(),
                        image.getIsSellable(),
                        false
                ));
            });
        } else {
            User user = userRepository.getUserByUsername(username);
            user.getUserData().getImages().stream().filter(e -> e.getImageData().getImageType().equals(ImageType.PUBLIC)).forEach(image -> {
                images.add(new DisplayImageTO(
                        image.getId().toString(),
                        "\\" + image.getImageData().getPath(),
                        image.getImageName(),
                        user.getUserData().getProfileImage() == null ? "" : "\\" + user.getUserData().getProfileImage().getPath(),
                        image.getDescription(),
                        user.getUsername(),
                        image.getLikeCount(),
                        image.getPrice(),
                        image.getIsSellable(),
                        true
                ));
            });
        }
        return images;
    }

    public DisplayImageTO getImageById(Integer id, String username) {
        Image image = imageRepository.getOne(Long.valueOf(id));
        return new DisplayImageTO(
                id.toString(),
                "\\" + image.getImageData().getPath(),
                image.getImageName(),
                image.getOwner().getProfileImage() == null ? "" : "\\" + image.getOwner().getProfileImage().getPath(),
                image.getDescription(),
                image.getOwner().getBaseUser().getUsername(),
                image.getLikeCount(),
                image.getPrice(),
                image.getIsSellable(),
                username.equalsIgnoreCase(image.getOwner().getBaseUser().getUsername())
        );
    }


    public void changeImageParams(EditImageParametersRequestTO request) {
        Image one = imageRepository.getOne(Long.valueOf(request.getId()));
        if (request.getName() != null && !request.getName().isBlank()) {
            one.setImageName(request.getName());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            one.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            one.setPrice(request.getPrice());
        }
        if (request.getStatus() != null) {
            one.setIsSellable(request.getStatus().getValue());
        }
        imageRepository.saveAndFlush(one);
    }

    @Transactional
    public PurchaseStatusTO buyImage(String productId, String username) {
        Image image = imageRepository.getOne(Long.valueOf(productId));
        if (!image.getIsSellable()) {
            return new PurchaseStatusTO(false, "Unavailable Product!");
        }
        BigDecimal amount = image.getPrice();
        UserData seller = image.getOwner();
        UserData buyer = userRepository.getUserByUsername(username).getUserData();

        if (buyer.getWallet().getBalance().subtract(amount).compareTo(BigDecimal.ZERO) >= 0) {
            buyer.getWallet().setBalance(buyer.getWallet().getBalance().subtract(amount));
            seller.getWallet().setBalance(seller.getWallet().getBalance().add(amount));
            image.setOwner(buyer);
            image.setIsSellable(false);
            TransactionHistory transaction = new TransactionHistory();
            transaction.setAmount(amount);
            transaction.setTransactionTime(LocalDateTime.now());
            transaction.setBuyer(buyer);
            transaction.setSeller(seller);
            transaction.setImage(image);
            transactionHistoryRepository.saveAndFlush(transaction);
            userDataRepository.saveAndFlush(seller);
            userDataRepository.saveAndFlush(buyer);
            imageRepository.saveAndFlush(image);
            return new PurchaseStatusTO(true, null);
        } else {
            return new PurchaseStatusTO(false, "Insufficient Funds!");
        }
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
        switch (type) {
            case "deposit":
                if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                transaction.setSeller(user.getUserData());
                transaction.setAmount(amount);
                wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
                walletRepository.saveAndFlush(wallet);
                transactionHistoryRepository.saveAndFlush(transaction);
                break;
            case "withdraw":
                if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0 || amount.compareTo(wallet.getBalance()) > 0) {
                    return;
                }
                transaction.setBuyer(user.getUserData());
                transaction.setAmount(amount);
                wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
                walletRepository.saveAndFlush(wallet);
                transactionHistoryRepository.saveAndFlush(transaction);
        }
    }

    public List<UserTO> getAllUsers(String username) {
        if (!userRepository.getUserByUsername(username).getRoles().contains(roleRepository.getOne(2))) {
            return null;
        } else {
            List<UserTO> users = new ArrayList<>();
            userRepository.findAll().forEach(e -> {
                users.add(
                        new UserTO(
                                e.getUsername(),
                                e.getUserData().getEmail(),
                                e.getUserData().getWallet().getId(),
                                e.getUserData().getWallet().getBalance()
                        )
                );
            });
            return users;
        }
    }
}
