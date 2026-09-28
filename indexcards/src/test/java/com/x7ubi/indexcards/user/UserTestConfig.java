package com.x7ubi.indexcards.user;

import com.x7ubi.indexcards.TestConfig;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;NON_KEYWORDS=USER"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class UserTestConfig extends TestConfig {

    protected static final String PASSWORD = "1234";

    @Autowired
    protected UserRepo userRepo;

    @Autowired
    protected ProjectRepo projectRepo;

    @Autowired
    protected IndexCardRepo indexCardRepo;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected User user;

    protected User user2;

    @BeforeEach
    void setupUsers() {
        this.indexCardRepo.deleteAll();
        this.projectRepo.deleteAll();
        this.userRepo.deleteAll();

        this.user = saveUser("test");
        this.user2 = saveUser("test2");
    }

    private User saveUser(String username) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setFirstname("Max");
        newUser.setSurname("Muster");
        newUser.setProjects(new ArrayList<>());
        newUser.setPassword(this.passwordEncoder.encode(PASSWORD));
        return this.userRepo.save(newUser);
    }
}
