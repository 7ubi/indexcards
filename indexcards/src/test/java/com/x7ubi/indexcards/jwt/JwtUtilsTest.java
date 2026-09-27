package com.x7ubi.indexcards.jwt;

import com.x7ubi.indexcards.TestConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@DirtiesContext
public class JwtUtilsTest extends TestConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    public void tokenSignedWithFormerHardcodedSecretIsRejectedTest() {
        // given: a token forged with the secret that used to be hardcoded in application.properties
        String forgedToken = Jwts.builder()
                .setSubject("victim")
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 60_000))
                .signWith(SignatureAlgorithm.HS512, "bezKoderSecretKey")
                .compact();

        // when
        boolean valid = this.jwtUtils.validateJwtToken(forgedToken);

        // then
        Assertions.assertFalse(valid);
    }
}
