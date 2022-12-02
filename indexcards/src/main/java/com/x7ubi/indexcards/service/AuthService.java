package com.x7ubi.indexcards.service;

import com.x7ubi.indexcards.exceptions.UsernameExistsException;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.auth.SignupRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AuthService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepo userRepo;

    public AuthService(PasswordEncoder passwordEncoder, UserRepo userRepo) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    @Transactional
    public void registerNewUserAccount(SignupRequest signupRequest) throws UsernameExistsException {
        User user = new User();

        if(userRepo.existsByUsername(signupRequest.getUsername())){
            throw new UsernameExistsException("Username already exists");
        }

        user.setUsername(signupRequest.getUsername());
        user.setFirstname(signupRequest.getFirstname());
        user.setSurname(signupRequest.getSurname());

        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        userRepo.save(user);
    }

}
