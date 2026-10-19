package com.x7ubi.indexcards.service.user;

import com.x7ubi.indexcards.exceptions.AuthentikMfaException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.user.MfaDeviceResponse;
import com.x7ubi.indexcards.service.authentication.AuthentikMfaDeviceService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MfaDeviceService extends AbstractUserService {

    private final AuthentikMfaDeviceService authentikMfaDeviceService;

    public MfaDeviceService(UserRepo userRepo, AuthentikMfaDeviceService authentikMfaDeviceService) {
        super(userRepo);
        this.authentikMfaDeviceService = authentikMfaDeviceService;
    }

    public List<MfaDeviceResponse> getMfaDevices(String username)
            throws EntityNotFoundException, AuthentikMfaException {
        User user = getUser(username);
        if (user.getOidcSubject() == null) {
            return List.of();
        }

        return authentikMfaDeviceService.getDevices(user.getOidcSubject());
    }
}
