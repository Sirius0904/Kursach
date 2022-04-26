package com.bsuir.sirius.service;

import com.bsuir.sirius.repository.RoleRepository;
import com.bsuir.sirius.repository.UserDataRepository;
import com.bsuir.sirius.repository.UserRepository;
import com.bsuir.sirius.entity.Role;
import com.bsuir.sirius.entity.User;
import com.bsuir.sirius.entity.UserData;
import com.bsuir.sirius.to.request.EditProfileUserDataTO;
import com.bsuir.sirius.to.request.RegisterUserRequestTO;
import com.bsuir.sirius.to.response.UserProfileInfoResponseTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserDataRepository userDataRepository;
    private final BCryptPasswordEncoder encoder;
    private final RoleRepository roleRepository;


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
        userDataRepository.saveAndFlush(userData);
        user.setPassword(encoder.encode(request.getPassword()));
        user.setUserData(userData);
        userRepository.saveAndFlush(user);
    }

    public UserProfileInfoResponseTO getUserInfo(String username) {
        User user = userRepository.getUserByUsername(username);
        UserProfileInfoResponseTO response = new UserProfileInfoResponseTO();

        if (user.getUserData() != null && user.getUserData().getFirstName() != null && user.getUserData().getLastName() != null) {
            response.setFio(user.getUserData().getFirstName() + " " + user.getUserData().getLastName());
            response.setEmail(user.getUserData().getEmail());
        }
        if (user.getUserData() != null) {
            if (user.getUserData().getCountry() != null && user.getUserData().getCountry().length() != 0) {
                response.setPlace(user.getUserData().getCountry());
            }
            if(user.getUserData().getCity() != null && user.getUserData().getCity().length() != 0){
                if(response.getPlace() == null || response.getPlace().length() == 0){
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

}
