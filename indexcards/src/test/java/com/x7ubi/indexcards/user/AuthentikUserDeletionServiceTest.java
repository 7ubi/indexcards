package com.x7ubi.indexcards.user;

import com.x7ubi.indexcards.exceptions.AuthentikDeletionException;
import com.x7ubi.indexcards.service.authentication.AuthentikUserDeletionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class AuthentikUserDeletionServiceTest {

    private static final String API = "http://authentik/api/v3";

    private static final String SUBJECT = "6a2f41a3-c54c-4fd4-9a2b-2f0b6c1e9a10";

    private static final String LOOKUP = API + "/core/users/?uuid=" + SUBJECT;

    private MockRestServiceServer server;

    private AuthentikUserDeletionService service;

    @BeforeEach
    void setup() {
        RestClient.Builder builder = RestClient.builder();
        this.server = MockRestServiceServer.bindTo(builder).build();
        this.service = new AuthentikUserDeletionService(builder, API, "secret-token");
    }

    @Test
    public void deletesUserFoundBySubjectTest() throws AuthentikDeletionException {
        this.server.expect(requestTo(LOOKUP)).andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer secret-token"))
                .andRespond(withSuccess("{\"results\":[{\"pk\":42,\"is_superuser\":false,\"username\":\"alice\"}]}",
                        MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(API + "/core/users/42/")).andExpect(method(HttpMethod.DELETE))
                .andExpect(header("Authorization", "Bearer secret-token"))
                .andRespond(withNoContent());

        this.service.deleteUser(SUBJECT);

        this.server.verify();
    }

    @Test
    public void missingUserCountsAsDeletedTest() throws AuthentikDeletionException {
        this.server.expect(requestTo(LOOKUP))
                .andRespond(withSuccess("{\"results\":[]}", MediaType.APPLICATION_JSON));

        this.service.deleteUser(SUBJECT);

        this.server.verify();
    }

    @Test
    public void superuserIsNotDeletedTest() throws AuthentikDeletionException {
        this.server.expect(requestTo(LOOKUP))
                .andRespond(withSuccess("{\"results\":[{\"pk\":1,\"is_superuser\":true}]}", MediaType.APPLICATION_JSON));

        this.service.deleteUser(SUBJECT);

        // no DELETE request expected
        this.server.verify();
    }

    @Test
    public void lookupErrorFailsTest() {
        this.server.expect(requestTo(LOOKUP)).andRespond(withServerError());

        Assertions.assertThrows(AuthentikDeletionException.class, () -> this.service.deleteUser(SUBJECT));
    }

    @Test
    public void deleteErrorFailsTest() {
        this.server.expect(requestTo(LOOKUP))
                .andRespond(withSuccess("{\"results\":[{\"pk\":42,\"is_superuser\":false}]}", MediaType.APPLICATION_JSON));
        this.server.expect(requestTo(API + "/core/users/42/")).andRespond(withServerError());

        Assertions.assertThrows(AuthentikDeletionException.class, () -> this.service.deleteUser(SUBJECT));
    }
}
