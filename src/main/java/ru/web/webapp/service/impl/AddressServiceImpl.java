package ru.web.webapp.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.web.webapp.io.entity.AddressEntity;
import ru.web.webapp.io.entity.UserEntity;
import ru.web.webapp.io.repositories.AddressRepository;
import ru.web.webapp.io.repositories.UserRepository;
import ru.web.webapp.service.AddressService;
import ru.web.webapp.shared.dto.AddressDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDTO> getAddresses(String userId) {
        List<AddressDTO> returnValue = new ArrayList<>();

        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null)
            return returnValue;

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        ModelMapper modelMapper = new ModelMapper();

        for (AddressEntity addressEntity : addresses) {
            returnValue.add(modelMapper.map(addressEntity, AddressDTO.class));
        }

        return returnValue;
    }

    @Override
    public AddressDTO getAddress(String addressId) {
        AddressDTO returnValue = null;

        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

        if (addressEntity != null)
            returnValue = new ModelMapper().map(addressEntity, AddressDTO.class);

        return returnValue;
    }
}
