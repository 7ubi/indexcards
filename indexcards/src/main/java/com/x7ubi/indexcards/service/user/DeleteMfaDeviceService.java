package com.x7ubi.indexcards.service.user;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.AuthentikMfaException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.MfaDeviceType;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.service.authentication.AuthentikMfaDeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeleteMfaDeviceService extends AbstractUserService {

    private final Logger logger = LoggerFactory.getLogger(DeleteMfaDeviceService.class);

    private final AuthentikMfaDeviceService authentikMfaDeviceService;

    public DeleteMfaDeviceService(UserRepo userRepo, AuthentikMfaDeviceService authentikMfaDeviceService) {
        super(userRepo);
        this.authentikMfaDeviceService = authentikMfaDeviceService;
    }

    public void deleteMfaDevice(String username, String type, String deviceId)
            throws EntityNotFoundException, AuthentikMfaException {
        User user = getUser(username);
        MfaDeviceType deviceType = MfaDeviceType.fromKey(type).orElseThrow(this::getMfaDeviceNotFoundError);

        if (user.getOidcSubject() == null
                || !authentikMfaDeviceService.deleteDevice(user.getOidcSubject(), deviceType, deviceId)) {
            throw getMfaDeviceNotFoundError();
        }

        logger.info("2FA device was removed");
    }

    private EntityNotFoundException getMfaDeviceNotFoundError() {
        logger.error(ErrorMessage.User.MFA_DEVICE_NOT_FOUND);
        return new EntityNotFoundException(ErrorMessage.User.MFA_DEVICE_NOT_FOUND);
    }
}
