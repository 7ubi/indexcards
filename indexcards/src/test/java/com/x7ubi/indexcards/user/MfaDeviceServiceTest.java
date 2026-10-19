package com.x7ubi.indexcards.user;

import com.x7ubi.indexcards.TestConfig;
import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.AuthentikMfaException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.MfaDeviceType;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.user.MfaDeviceResponse;
import com.x7ubi.indexcards.service.authentication.AuthentikMfaDeviceService;
import com.x7ubi.indexcards.service.user.DeleteMfaDeviceService;
import com.x7ubi.indexcards.service.user.MfaDeviceService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:mfadevicetestdb;NON_KEYWORDS=USER"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
// Own database and AFTER_EACH like DeleteUserServiceTest: the mocked bean gives this class its own context, which must
// not stay cached next to the contexts that share "testdb".
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MfaDeviceServiceTest extends TestConfig {

    private static final String SUBJECT = "6a2f41a3-c54c-4fd4-9a2b-2f0b6c1e9a10";

    // The real service would call Authentik.
    @MockitoBean
    private AuthentikMfaDeviceService authentikMfaDeviceService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MfaDeviceService mfaDeviceService;

    @Autowired
    private DeleteMfaDeviceService deleteMfaDeviceService;

    private User user;

    @BeforeEach
    void setupUser() {
        this.userRepo.deleteAll();

        this.user = new User();
        this.user.setUsername("test");
        this.user.setFirstname("Max");
        this.user.setSurname("Muster");
        this.user.setProjects(new ArrayList<>());
        this.user = this.userRepo.save(this.user);
    }

    @AfterEach
    void clearDatabase() {
        this.userRepo.deleteAll();
    }

    private void linkToAuthentik() {
        this.user.setOidcSubject(SUBJECT);
        this.user = this.userRepo.save(this.user);
    }

    @Test
    public void getDevicesOfLinkedUserTest() throws EntityNotFoundException, AuthentikMfaException {
        linkToAuthentik();
        List<MfaDeviceResponse> devices = List.of(new MfaDeviceResponse("totp", "1", "Phone", "2026-10-19", null));
        when(this.authentikMfaDeviceService.getDevices(SUBJECT)).thenReturn(devices);

        Assertions.assertEquals(devices, this.mfaDeviceService.getMfaDevices("test"));
    }

    @Test
    public void userWithoutAuthentikAccountHasNoDevicesTest() throws EntityNotFoundException, AuthentikMfaException {
        Assertions.assertTrue(this.mfaDeviceService.getMfaDevices("test").isEmpty());
        verifyNoInteractions(this.authentikMfaDeviceService);
    }

    @Test
    public void getDevicesOfUnknownUserFailsTest() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> this.mfaDeviceService.getMfaDevices("nobody"));
    }

    @Test
    public void deleteDeviceTest() throws EntityNotFoundException, AuthentikMfaException {
        linkToAuthentik();
        when(this.authentikMfaDeviceService.deleteDevice(SUBJECT, MfaDeviceType.EMAIL, "5")).thenReturn(true);

        this.deleteMfaDeviceService.deleteMfaDevice("test", "email", "5");

        verify(this.authentikMfaDeviceService).deleteDevice(SUBJECT, MfaDeviceType.EMAIL, "5");
    }

    @Test
    public void deleteUnknownDeviceFailsTest() throws AuthentikMfaException {
        linkToAuthentik();
        when(this.authentikMfaDeviceService.deleteDevice(any(), any(), anyString())).thenReturn(false);

        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                () -> this.deleteMfaDeviceService.deleteMfaDevice("test", "totp", "99"));
        Assertions.assertEquals(ErrorMessage.User.MFA_DEVICE_NOT_FOUND, exception.getMessage());
    }

    @Test
    public void deleteDeviceOfUnknownTypeFailsTest() {
        linkToAuthentik();

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> this.deleteMfaDeviceService.deleteMfaDevice("test", "sms", "1"));
        verifyNoInteractions(this.authentikMfaDeviceService);
    }

    @Test
    public void deleteDeviceOfUserWithoutAuthentikAccountFailsTest() {
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> this.deleteMfaDeviceService.deleteMfaDevice("test", "totp", "1"));
        verifyNoInteractions(this.authentikMfaDeviceService);
    }
}
