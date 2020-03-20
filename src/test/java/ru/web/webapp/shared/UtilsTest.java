package ru.web.webapp.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {

    @Autowired
    Utils utils;

    @BeforeEach
    void setUp() throws Exception {

    }

    @Test
    void generateUserId() {
        String userId = utils.generateUserId(30);
        String userId2 = utils.generateUserId(30);

        assertNotNull(userId);
        assertNotNull(userId2);

        assertEquals(30, userId.length());
        assertFalse(userId.equalsIgnoreCase(userId2));

    }

    @Test
    void hasTokenNotExpired() {
        String token = utils.generateEmailVerificationToken("sadsafwer");
        assertNotNull(token);

        boolean hasTokenExpired = Utils.hasTokenExpired(token);

        assertFalse(hasTokenExpired);
    }

    @Test
    void hasTokenExpired() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJNMXZ4bFNobzFvMml0OVdTVUVpd3g3VHhHYlBxWjQiLCJleHAiOjE1ODUyMjI" +
                "2NDd9.-CURUzWE1cX23sGPVOrhxN7UbAkDzfT5tK5jQwu5USMo-am5yPSwVj67X1DROrK9PlcKuSA7U8w_-Xw_D805Ig";

        boolean hasTokenExpired = Utils.hasTokenExpired(token);

        assertTrue(hasTokenExpired);
    }
}