package com.x7ubi.indexcards.service.authentication;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.UsernameExistsException;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.auth.SignupRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class AuthService {

    Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final PasswordEncoder passwordEncoder;

    private final UserRepo userRepo;

    public AuthService(PasswordEncoder passwordEncoder, UserRepo userRepo) {
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    @Transactional
    public void registerNewUserAccount(SignupRequest signupRequest) throws UsernameExistsException {
        User user = new User();

        if (userRepo.existsByUsername(signupRequest.getUsername())) {
            logger.error(ErrorMessage.Authentication.USERNAME_EXITS);
            throw new UsernameExistsException(ErrorMessage.Authentication.USERNAME_EXITS);
        }

        user.setUsername(signupRequest.getUsername());
        user.setFirstname(signupRequest.getFirstname());
        user.setSurname(signupRequest.getSurname());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        userRepo.save(user);

        logger.info("User was created");
    }

}
