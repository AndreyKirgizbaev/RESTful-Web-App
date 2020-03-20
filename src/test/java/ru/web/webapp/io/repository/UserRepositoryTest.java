package ru.web.webapp.io.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.web.webapp.io.entity.AddressEntity;
import ru.web.webapp.io.entity.UserEntity;
import ru.web.webapp.io.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    static boolean recordsCreated = false;

    @BeforeEach
    void setUp() throws Exception {
        if (!recordsCreated) createRecords();
    }

    @Test
    void testGetVerifiedUsers() {

        Pageable pageable = PageRequest.of(0, 2);
        Page<UserEntity> pages = userRepository.findAllByUsersWithConfirmedEmailAddresses(pageable);

        assertNotNull(pages);

        List<UserEntity> userEntities = pages.getContent();
        assertNotNull(userEntities);
        assertEquals(2, userEntities.size());
    }

    @Test
    void testFindUserByFirstName() {

        String firstName = "Andrey";

        List<UserEntity> users = userRepository.findAllByFirstName(firstName);

        assertNotNull(users);
        assertEquals(2, users.size());

        UserEntity user = users.get(0);
        assertEquals(user.getFirstName(), firstName);
    }

    @Test
    void testFindUserByLastName() {

        String lastName = "Gagovich";

        List<UserEntity> users = userRepository.findAllByLastName(lastName);

        assertNotNull(users);
        assertEquals(2, users.size());

        UserEntity user = users.get(0);
        assertEquals(user.getLastName(), lastName);
    }

    @Test
    void testFindUserByKeyword() {

        String keyword = "Gag";

        List<UserEntity> users = userRepository.findAllByKeyword(keyword);

        assertNotNull(users);
        assertEquals(2, users.size());

        UserEntity user = users.get(0);
        assertTrue(user.getLastName().contains(keyword) || user.getFirstName().contains(keyword));
    }

    @Test
    void testUserFirstNameAndLastNameByKeyword() {

        String keyword = "Gag";

        List<Object[]> users = userRepository.findUserFirstNameAndLastNameByKeyword(keyword);

        assertNotNull(users);
        assertEquals(2, users.size());

        Object[] user = users.get(0);

        assertEquals(2, user.length);

        String firstName = String.valueOf(user[0]);
        String lastName = String.valueOf(user[1]);

        assertNotNull(firstName);
        assertNotNull(lastName);
    }

    @Test
    void testUpdateUserEmailVerificationStatus() {

        boolean emailVerificationStatus = false;

        userRepository.updateUserEmailVerificationStatus(emailVerificationStatus, "123321");

        UserEntity storedUserDetails = userRepository.findByUserId("123321");

        boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();

        assertEquals(storedEmailVerificationStatus, emailVerificationStatus);
    }

    @Test
    void testFindUserEntityByUserId() {

        String userId = "123321";

        UserEntity userEntity = userRepository.findUserEntityByUserId(userId);

        assertNotNull(userEntity);

        assertEquals(userId, userEntity.getUserId());
    }

    @Test
    void testFindUserEntityFullNameById() {

        String userId = "123321";

        List<Object[]> records = userRepository.findUserEntityFullNameById(userId);

        assertNotNull(records);
        assertEquals(1, records.size());

        Object[] userDetails = records.get(0);

        String firstName = String.valueOf(userDetails[0]);
        String lastName = String.valueOf(userDetails[1]);

        assertNotNull(firstName);
        assertNotNull(lastName);
    }

    @Test
    void testUpdateUserEntityEmailVerificationStatus() {

        boolean emailVerificationStatus = false;

        userRepository.updateUserEntityEmailVerificationStatus(emailVerificationStatus, "123321");

        UserEntity storedUserDetails = userRepository.findByUserId("123321");

        boolean storedEmailVerificationStatus = storedUserDetails.getEmailVerificationStatus();

        assertEquals(storedEmailVerificationStatus, emailVerificationStatus);
    }

    private void createRecords() {

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("Andrey");
        userEntity.setLastName("Gagovich");
        userEntity.setUserId("123321");
        userEntity.setEncryptedPassword("123");
        userEntity.setEmail("test@test.com");
        userEntity.setEmailVerificationStatus(true);

        AddressEntity addressEntity = new AddressEntity();
        addressEntity.setType("shipping");
        addressEntity.setAddressId("asdsfa");
        addressEntity.setCity("Moscow");
        addressEntity.setCountry("Russia");
        addressEntity.setPostalCode("123100");
        addressEntity.setStreetName("123 Street");

        List<AddressEntity> addresses = new ArrayList<>();
        addresses.add(addressEntity);

        userEntity.setAddresses(addresses);

        userRepository.save(userEntity);

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setFirstName("Andrey");
        userEntity2.setLastName("Gagovich");
        userEntity2.setUserId("321123");
        userEntity2.setEncryptedPassword("123");
        userEntity2.setEmail("test2@test.com");
        userEntity2.setEmailVerificationStatus(true);

        AddressEntity addressEntity2 = new AddressEntity();
        addressEntity2.setType("shipping");
        addressEntity2.setAddressId("asdsfasadADS");
        addressEntity2.setCity("Moscow");
        addressEntity2.setCountry("Russia");
        addressEntity2.setPostalCode("123100");
        addressEntity2.setStreetName("123 Street");

        List<AddressEntity> addresses2 = new ArrayList<>();
        addresses2.add(addressEntity2);

        userEntity2.setAddresses(addresses2);

        userRepository.save(userEntity2);

        recordsCreated = true;
    }
}
