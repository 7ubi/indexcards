package com.x7ubi.indexcards.user;

import com.x7ubi.indexcards.exceptions.AuthentikMfaException;
import com.x7ubi.indexcards.models.MfaDeviceType;
import com.x7ubi.indexcards.response.user.MfaDeviceResponse;
import com.x7ubi.indexcards.service.authentication.AuthentikMfaDeviceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class AuthentikMfaDeviceServiceTest {

    private static final String API = "http://authentik/api/v3";

    private static final String SUBJECT = "6a2f41a3-c54c-4fd4-9a2b-2f0b6c1e9a10";

    private static final String LOOKUP = API + "/core/users/?uuid=" + SUBJECT;

    private static final String USER = "{\"results\":[{\"pk\":42,\"uuid\":\"" + SUBJECT + "\",\"is_superuser\":false}]}";

    private static final String DEVICES = API + "/authenticators/admin/all/?user=42";

    private static final String DEVICE_LIST = "["
            + "{\"pk\":\"1\",\"name\":\"Phone\",\"meta_model_name\":\"authentik_stages_authenticator_totp.totpdevice\","
            + "\"confirmed\":true,\"created\":\"2026-10-19T13:06:37Z\",\"last_used\":null},"
            + "{\"pk\":\"2\",\"name\":\"YubiKey\",\"meta_model_name\":\"authentik_stages_authenticator_webauthn.webauthndevice\","
            + "\"confirmed\":true,\"created\":\"2026-10-18T10:00:00Z\",\"last_used\":\"2026-10-19T09:00:00Z\"},"
            + "{\"pk\":\"3\",\"name\":\"Unfinished\",\"meta_model_name\":\"authentik_stages_authenticator_totp.totpdevice\","
            + "\"confirmed\":false,\"created\":\"2026-10-19T13:00:00Z\",\"last_used\":null},"
            + "{\"pk\":\"4\",\"name\":\"Static Token\",\"meta_model_name\":\"authentik_stages_authenticator_static.staticdevice\","
            + "\"confirmed\":true,\"created\":\"2026-10-19T13:00:00Z\",\"last_used\":null},"
            + "{\"pk\":\"5\",\"name\":\"Duo\",\"meta_model_name\":\"authentik_stages_authenticator_duo.duodevice\","
            + "\"confirmed\":true,\"created\":\"2026-10-19T13:00:00Z\",\"last_used\":null}"
            + "]";

    private MockRestServiceServer server;

    private AuthentikMfaDeviceService service;

    @BeforeEach
    void setup() {
        RestClient.Builder builder = RestClient.builder();
        this.server = MockRestServiceServer.bindTo(builder).build();
        this.service = new AuthentikMfaDeviceService(builder, API, "secret-token");
    }

    @Test
    public void listsConfirmedSupportedDevicesTest() throws AuthentikMfaException {
        this.server.expect(requestTo(LOOKUP)).andExpect(header("Authorization", "Bearer secret-token"))
                .andRespond(withSuccess(USER, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(DEVICES)).andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer secret-token"))
                .andRespond(withSuccess(DEVICE_LIST, MediaType.APPLICATION_JSON));

        List<MfaDeviceResponse> devices = this.service.getDevices(SUBJECT);

        Assertions.assertEquals(3, devices.size());
        Assertions.assertEquals("totp", devices.get(0).getType());
        Assertions.assertEquals("1", devices.get(0).getId());
        Assertions.assertEquals("Phone", devices.get(0).getName());
        Assertions.assertNull(devices.get(0).getLastUsed());
        Assertions.assertEquals("webauthn", devices.get(1).getType());
        Assertions.assertEquals("2026-10-19T09:00:00Z", devices.get(1).getLastUsed());
        Assertions.assertEquals("static", devices.get(2).getType());
        Assertions.assertEquals("4", devices.get(2).getId());
        this.server.verify();
    }

    @Test
    public void missingUserHasNoDevicesTest() throws AuthentikMfaException {
        this.server.expect(requestTo(LOOKUP))
                .andRespond(withSuccess("{\"results\":[]}", MediaType.APPLICATION_JSON));

        Assertions.assertTrue(this.service.getDevices(SUBJECT).isEmpty());
        this.server.verify();
    }

    @Test
    public void otherUsersUuidHasNoDevicesTest() throws AuthentikMfaException {
        this.server.expect(requestTo(LOOKUP))
                .andRespond(withSuccess("{\"results\":[{\"pk\":7,\"uuid\":\"00000000-0000-0000-0000-000000000000\","
                        + "\"is_superuser\":false}]}", MediaType.APPLICATION_JSON));

        Assertions.assertTrue(this.service.getDevices(SUBJECT).isEmpty());
        // no device request expected
        this.server.verify();
    }

    @Test
    public void deletesOwnDeviceTest() throws AuthentikMfaException {
        this.server.expect(requestTo(LOOKUP)).andRespond(withSuccess(USER, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(DEVICES)).andRespond(withSuccess(DEVICE_LIST, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(API + "/authenticators/admin/webauthn/2/")).andExpect(method(HttpMethod.DELETE))
                .andExpect(header("Authorization", "Bearer secret-token"))
                .andRespond(withNoContent());

        Assertions.assertTrue(this.service.deleteDevice(SUBJECT, MfaDeviceType.WEBAUTHN, "2"));
        this.server.verify();
    }

    @Test
    public void deletesOwnRecoveryCodesTest() throws AuthentikMfaException {
        this.server.expect(requestTo(LOOKUP)).andRespond(withSuccess(USER, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(DEVICES)).andRespond(withSuccess(DEVICE_LIST, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(API + "/authenticators/admin/static/4/")).andExpect(method(HttpMethod.DELETE))
                .andRespond(withNoContent());

        Assertions.assertTrue(this.service.deleteDevice(SUBJECT, MfaDeviceType.STATIC, "4"));
        this.server.verify();
    }

    @Test
    public void doesNotDeleteForeignDeviceTest() throws AuthentikMfaException {
        this.server.expect(requestTo(LOOKUP)).andRespond(withSuccess(USER, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(DEVICES)).andRespond(withSuccess(DEVICE_LIST, MediaType.APPLICATION_JSON));

        // Device 99 is not in the user's list.
        Assertions.assertFalse(this.service.deleteDevice(SUBJECT, MfaDeviceType.TOTP, "99"));
        this.server.verify();
    }

    @Test
    public void doesNotDeleteDeviceOfOtherTypeTest() throws AuthentikMfaException {
        this.server.expect(requestTo(LOOKUP)).andRespond(withSuccess(USER, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(DEVICES)).andRespond(withSuccess(DEVICE_LIST, MediaType.APPLICATION_JSON));

        // pk 1 is a TOTP device of the user; a WebAuthn device with pk 1 could belong to someone else.
        Assertions.assertFalse(this.service.deleteDevice(SUBJECT, MfaDeviceType.WEBAUTHN, "1"));
        this.server.verify();
    }

    @Test
    public void listErrorFailsTest() {
        this.server.expect(requestTo(LOOKUP)).andRespond(withSuccess(USER, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(DEVICES)).andRespond(withServerError());

        Assertions.assertThrows(AuthentikMfaException.class, () -> this.service.getDevices(SUBJECT));
    }

    @Test
    public void deleteErrorFailsTest() {
        this.server.expect(requestTo(LOOKUP)).andRespond(withSuccess(USER, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(DEVICES)).andRespond(withSuccess(DEVICE_LIST, MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(API + "/authenticators/admin/totp/1/")).andRespond(withServerError());

        Assertions.assertThrows(AuthentikMfaException.class,
                () -> this.service.deleteDevice(SUBJECT, MfaDeviceType.TOTP, "1"));
    }
}
