package com.x7ubi.indexcards.jwt;

import com.x7ubi.indexcards.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;NON_KEYWORDS=USER"
})
@DirtiesContext
public class JwtUtilsTest extends TestConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    public void tokenSignedWithFormerHardcodedSecretIsRejectedTest() throws Exception {
        // given: a token forged with the secret that used to be hardcoded in application.properties. jjwt refuses to
        // sign with such a weak key, so the HS512 signature is computed by hand. jjwt 0.9 read the secret as Base64 and
        // silently dropped the incomplete 17th character, which the strict JDK decoder rejects.
        long now = System.currentTimeMillis() / 1000;
        Base64.Encoder base64Url = Base64.getUrlEncoder().withoutPadding();
        String unsignedToken = base64Url.encodeToString("{\"alg\":\"HS512\"}".getBytes(StandardCharsets.UTF_8)) + "."
                + base64Url.encodeToString(("{\"sub\":\"victim\",\"iat\":" + now + ",\"exp\":" + (now + 60) + "}")
                .getBytes(StandardCharsets.UTF_8));
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(new SecretKeySpec(Base64.getDecoder().decode("bezKoderSecretKe"), "HmacSHA512"));
        String forgedToken = unsignedToken + "."
                + base64Url.encodeToString(mac.doFinal(unsignedToken.getBytes(StandardCharsets.US_ASCII)));

        // when
        boolean valid = this.jwtUtils.validateJwtToken(forgedToken);

        // then
        Assertions.assertFalse(valid);
    }

    @Test
    public void generateJwtTokenForUsernameTest() {
        // when
        String token = this.jwtUtils.generateJwtTokenForUsername("someone");

        // then
        Assertions.assertTrue(this.jwtUtils.validateJwtToken(token));
        Assertions.assertEquals("someone", this.jwtUtils.getUsernameFromJwtToken(token));
    }
}
