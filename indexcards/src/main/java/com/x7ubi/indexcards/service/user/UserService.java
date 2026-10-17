package com.x7ubi.indexcards.service.user;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.user.UserResponse;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UserService extends AbstractUserService {

    public UserService(UserRepo userRepo) {
        super(userRepo);
    }

    @Transactional
    public UserResponse getUserResponse(String username) throws EntityNotFoundException {
        return toUserResponse(getUser(username));
    }
}
