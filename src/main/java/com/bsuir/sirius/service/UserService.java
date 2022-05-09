package com.bsuir.sirius.service;

import com.bsuir.sirius.entity.*;
import com.bsuir.sirius.entity.Image;
import com.bsuir.sirius.enumeration.ImageType;
import com.bsuir.sirius.repository.*;
import com.bsuir.sirius.to.mvc.request.*;
import com.bsuir.sirius.to.mvc.response.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


    /*
     * Поиск пользователя в бд по юзернейму
     *
     * Используется для получение имени пользователя в навигационной панели приложения
     *
     * */
    public User getCurrentUser(String username) {
        return userRepository.getUserByUsername(username);
    }


    /*
     * Регистрация пользователя
     *
     * */
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
        user.setPassword(encoder.encode(request.getPassword()));
        user.setUsername(request.getUsername());

        UserData userData = new UserData();
        userData.setEmail(request.getEmail());

        ImageData imageData = new ImageData().setImageType(ImageType.PROFILE).setPath("user-photos\\1\\unknown.png");
        userData.setProfileImage(imageData);

        Wallet wallet = new Wallet();
        wallet.setId(UUID.randomUUID().toString());
        userData.setWallet(wallet);

        user.setUserData(userData);

        imageDataRepository.saveAndFlush(imageData);
        walletRepository.saveAndFlush(wallet);
        userDataRepository.saveAndFlush(userData);
        userRepository.saveAndFlush(user);
    }


    /*
     * Получение пользовательской информации для профиля
     *
     *  Логин
     *  Мейл
     *  Фио
     *  локация
     *  аватарка
     *
     * */
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


    /*
     * Информация для страницы редактирования
     *
     * */
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


    /*
     * Сохрание внесенных изменений на странице редактирования
     * */
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


    /*
     * Список последних транзакций для страницы кошелька
     * */
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

    /*
     * Количество транзакций совершенных пользователем
     * */
    public Integer getTransactionCount(String username) {
        User user = userRepository.getUserByUsername(username);
        Integer id = user.getUserData().getId();
        return transactionHistoryRepository.findAllByBuyerIdOrSellerId(id, id).size();
    }

    /*
     * Загрузка аватарки
     *
     * */
    public void uploadImage(MultipartFile file, String username) throws IOException {
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


    /*
     *  Загрузка изображения в галерею
     * */
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


    /*
     * Получение изображений пользователя/всех
     * */
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


    /*
     * Получение информации по картинке по ее ID
     * */
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


    /*
     * Изменение параметров изображения
     * */
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


    /*
     * Покупка картинки
     * */
    @Transactional
    public PurchaseStatusTO buyImage(String productId, String username) throws Exception {
        Image image = imageRepository.getOne(Long.valueOf(productId));
        if (!image.getIsSellable()) {
            return new PurchaseStatusTO(false, "Unavailable Product!", null);
        }
        BigDecimal amount = image.getPrice();
        UserData seller = image.getOwner();
        UserData buyer = userRepository.getUserByUsername(username).getUserData();

        if (buyer.getWallet().getBalance().subtract(amount).compareTo(BigDecimal.ZERO) >= 0) {
            buyer.getWallet().setBalance(buyer.getWallet().getBalance().subtract(amount));
            seller.getWallet().setBalance(seller.getWallet().getBalance().add(amount));
            String mailTo = image.getOwner().getEmail();
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


            /* pdf)) */
            Document document = new Document();
            String path = "user-totals/" + username;

            Path uploadPath = Paths.get(path);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            path = path + "/" + username + "_buy_" + image.getId() + "_total.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(path));

            document.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
            document.add(new Paragraph("Good deal! \n", font));

            PdfPTable table = new PdfPTable(3);
            Stream.of("Username", "Product", "Price")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(2);
                        header.setPhrase(new Phrase(columnTitle));
                        table.addCell(header);
                    });
            table.addCell(username);
            table.addCell(image.getImageName());
            table.addCell(amount.toString());
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(table);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Thanks for using our service!\n", font));
            document.close();
            /*pdf end*/
            return new PurchaseStatusTO(true, null, path);
        } else {
            return new PurchaseStatusTO(false, "Insufficient Funds!", null);
        }
    }


    @Transactional
    public void walletAction(String type, String username, DepositFormTO form, BigDecimal amount) {
        User user = userRepository.getUserByUsername(username);
        TransactionHistory transaction = new TransactionHistory();
        transaction.setTransactionTime(LocalDateTime.now());
        Wallet wallet = user.getUserData().getWallet();
        switch (type) {
            case "deposit":
                if (form.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                    return;
                }
                transaction.setSeller(user.getUserData());
                transaction.setAmount(form.getAmount());
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

    public List<UserTO> getAllUsers() {
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


    public boolean isOwner(String username, Integer imageId) {
        Image one = imageRepository.getOne(Long.valueOf(imageId));
        if (one == null) {
            return false;
        }
        return one.getOwner().getBaseUser().getUsername().equals(username);
    }

    public List<DisplayImageTO> findImage(String name, String username) {
        List<DisplayImageTO> result = new ArrayList<>();
        if (username != null) {
            imageRepository.findAllByImageNameContaining(name).forEach(image -> {
                result.add(new DisplayImageTO(
                        image.getId().toString(),
                        "\\" + image.getImageData().getPath(),
                        image.getImageName(),
                        image.getOwner().getProfileImage() == null ? "" : "\\" + image.getOwner().getProfileImage().getPath(),
                        image.getDescription(),
                        image.getOwner().getBaseUser().getUsername(),
                        image.getLikeCount(),
                        image.getPrice(),
                        image.getIsSellable(),
                        username.equalsIgnoreCase(image.getOwner().getBaseUser().getUsername())
                ));
            });
        } else {
            imageRepository.findAllByImageNameContaining(name).forEach(image -> {
                result.add(new DisplayImageTO(
                        image.getId().toString(),
                        "\\" + image.getImageData().getPath(),
                        image.getImageName(),
                        image.getOwner().getProfileImage() == null ? "" : "\\" + image.getOwner().getProfileImage().getPath(),
                        image.getDescription(),
                        image.getOwner().getBaseUser().getUsername(),
                        image.getLikeCount(),
                        image.getPrice(),
                        image.getIsSellable(),
                        false
                ));
            });
        }
        return result;
    }
}
