package com.bsuir.sirius.service;

import com.bsuir.sirius.dao.RoleRepository;
import com.bsuir.sirius.dao.UserDataRepository;
import com.bsuir.sirius.dao.UserRepository;
import com.bsuir.sirius.entity.Role;
import com.bsuir.sirius.entity.User;
import com.bsuir.sirius.entity.UserData;
import com.bsuir.sirius.to.request.RegisterUserRequestTO;
import com.bsuir.sirius.to.response.UserInfoResponseTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserDataRepository userDataRepository;
    private final BCryptPasswordEncoder encoder;
    private final RoleRepository roleRepository;


    public void registerUser(RegisterUserRequestTO request) throws Exception {
        if(userRepository.getUserByUsername(request.getUsername()) != null){
            throw new Exception("User already registered");
        }
        if(!request.getPassword().equals(request.getConfirmation())){
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

    public UserInfoResponseTO getUserInfo(String username){
        User user = userRepository.getUserByUsername(username);
        UserInfoResponseTO response = new UserInfoResponseTO();

        if(user.getUserData() != null ){
            response.setFirstName(user.getUserData().getFirstName() == null ? "" : user.getUserData().getFirstName());
            response.setLastName(user.getUserData().getLastName() == null ? "" : user.getUserData().getLastName());
            response.setEmail(user.getUserData().getEmail() == null ? "" : user.getUserData().getEmail());
        }
        response.setUsername(user.getUsername());

        return response;
    }
}
