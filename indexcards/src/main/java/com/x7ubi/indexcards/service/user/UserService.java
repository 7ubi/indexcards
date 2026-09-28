package com.x7ubi.indexcards.service.user;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.user.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UserService extends AbstractUserService {

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        super(userRepo, passwordEncoder);
    }

    @Transactional
    public UserResponse getUserResponse(String username) throws EntityNotFoundException {
        User user = getUser(username);

        return new UserResponse(user.getUsername(), user.getFirstname(), user.getSurname());
    }
}
