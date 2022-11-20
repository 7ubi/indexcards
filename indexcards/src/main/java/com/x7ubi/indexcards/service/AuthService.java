package com.x7ubi.indexcards.service;

import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service

public class AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    @Transactional
    public User registerNewUserAccount(SignupRequest signupRequest) {
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setFirstname(signupRequest.getFirstname());
        user.setSurname(signupRequest.getSurname());

        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        return userRepo.save(user);
    }
}
