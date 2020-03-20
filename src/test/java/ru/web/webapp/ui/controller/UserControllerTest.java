package ru.web.webapp.ui.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.web.webapp.service.impl.UserServiceImpl;
import ru.web.webapp.shared.dto.AddressDTO;
import ru.web.webapp.shared.dto.UserDTO;
import ru.web.webapp.ui.model.response.UserRest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserServiceImpl userService;

    UserDTO userDTO;

    final String USER_ID = "43241234124";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        userDTO = new UserDTO();
        userDTO.setFirstName("test");
        userDTO.setLastName("test");
        userDTO.setEmail("test@test.com");
        userDTO.setEmailVerificationStatus(false);
        userDTO.setEmailVerificationToken(null);
        userDTO.setUserId(USER_ID);
        userDTO.setAddresses(getAddressesDTO());
        userDTO.setEncryptedPassword("weqer");
    }

    @Test
    void getUser() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDTO);

        UserRest userRest = userController.getUser(USER_ID);

        assertNotNull(userRest);

        assertEquals(USER_ID, userRest.getUserId());
        assertEquals(userDTO.getFirstName(), userRest.getFirstName());
        assertEquals(userDTO.getLastName(), userRest.getLastName());
        assertEquals(userDTO.getAddresses().size(), userRest.getAddresses().size());
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
}