package ru.web.webapp.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.web.webapp.exceptions.UserServiceException;
import ru.web.webapp.io.entity.AddressEntity;
import ru.web.webapp.io.entity.UserEntity;
import ru.web.webapp.io.repositories.UserRepository;
import ru.web.webapp.shared.AmazonSES;
import ru.web.webapp.shared.Utils;
import ru.web.webapp.shared.dto.AddressDTO;
import ru.web.webapp.shared.dto.UserDTO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    AmazonSES amazonSES;

    String userId = "43241234124";
    String encryptedPassword = "encryptedPassword";

    UserEntity userEntity;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("test");
        userEntity.setLastName("test");
        userEntity.setUserId(userId);
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmail("test@gmail.com");
        userEntity.setEmailVerificationToken("1214324");
        userEntity.setAddresses(getAddressesEntity());
    }

    @Test
    void getUser() {

        when(userRepository.findUserByEmail(anyString())).thenReturn(userEntity);

        UserDTO userDTO = userService.getUser("test@test.com");

        assertNotNull(userDTO);
        assertEquals("test", userDTO.getFirstName());

    }

    @Test
    void getUser_UsernameNotFoundException() {

        when(userRepository.findUserByEmail(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUser("test@test.com");
        });

    }

    @Test
    void createUser() {

        when(userRepository.findUserByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("1231244234");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDTO.class));

        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserDTO userDTO = new UserDTO();
        userDTO.setAddresses(getAddressesDTO());
        userDTO.setFirstName("test");
        userDTO.setLastName("test");
        userDTO.setPassword("123");
        userDTO.setEmail("test@gmail.com");

        UserDTO storedUserDTO = userService.createUser(userDTO);

        assertNotNull(storedUserDTO);
        assertNotNull(storedUserDTO.getUserId());

        assertEquals(userEntity.getFirstName(), storedUserDTO.getFirstName());
        assertEquals(userEntity.getLastName(), storedUserDTO.getLastName());

        assertEquals(storedUserDTO.getAddresses().size(), userEntity.getAddresses().size());

        verify(utils, times(storedUserDTO.getAddresses().size())).generateAddressId(30);
        verify(bCryptPasswordEncoder, times(1)).encode("123");
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void createUser_UserServiceException() {

        when(userRepository.findUserByEmail(anyString())).thenReturn(userEntity);

        UserDTO userDTO = new UserDTO();
        userDTO.setAddresses(getAddressesDTO());
        userDTO.setFirstName("test");
        userDTO.setLastName("test");
        userDTO.setPassword("123");
        userDTO.setEmail("test@gmail.com");

        assertThrows(UserServiceException.class, () -> {
            userService.createUser(userDTO);
        });

    }

    private List<AddressDTO> getAddressesDTO() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setType("shipping");
        addressDTO.setCity("Moscow");
        addressDTO.setCountry("Russia");
        addressDTO.setPostalCode("123100");
        addressDTO.setStreetName("123 Street");

        AddressDTO billingAddressDTO = new AddressDTO();
        addressDTO.setType("billing");
        addressDTO.setCity("Moscow");
        addressDTO.setCountry("Russia");
        addressDTO.setPostalCode("123100");
        addressDTO.setStreetName("123 Street");

        List<AddressDTO> addresses = new ArrayList<>();
        addresses.add(addressDTO);
        addresses.add(billingAddressDTO);

        return addresses;
    }

    private List<AddressEntity> getAddressesEntity() {

        List<AddressDTO> addresses = getAddressesDTO();
        Type listType = new TypeToken<List<AddressEntity>>() {}.getType();

        return new ModelMapper().map(addresses, listType);
    }
}