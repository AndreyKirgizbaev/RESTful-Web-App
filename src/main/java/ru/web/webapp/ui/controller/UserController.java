package ru.web.webapp.ui.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.web.webapp.exceptions.UserServiceException;
import ru.web.webapp.service.AddressService;
import ru.web.webapp.shared.dto.AddressDTO;
import ru.web.webapp.ui.model.request.PasswordResetModel;
import ru.web.webapp.ui.model.request.PasswordResetRequestModel;
import ru.web.webapp.ui.model.request.UserDetailsRequestModel;
import ru.web.webapp.ui.model.response.*;
import ru.web.webapp.service.UserService;
import ru.web.webapp.shared.dto.UserDTO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    @ApiOperation(value="The Get User Details Web Service Endpoint",
            notes="${userController.GetUser.ApiOperation.Notes}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUser(@PathVariable String id) {

        UserRest returnValue;

        UserDTO userDTO = userService.getUserByUserId(id);
        ModelMapper modelMapper = new ModelMapper();
        returnValue = modelMapper.map(userDTO, UserRest.class);

        return returnValue;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws UserServiceException {

        if (userDetails.getFirstName().isEmpty())
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());

        ModelMapper modelMapper = new ModelMapper();
        UserDTO userDto = modelMapper.map(userDetails, UserDTO.class);

        UserDTO createdUser = userService.createUser(userDto);
        UserRest returnValue = modelMapper.map(createdUser, UserRest.class);

        return returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @PutMapping(path = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

        UserRest returnValue = new UserRest();

        UserDTO userDto = new ModelMapper().map(userDetails, UserDTO.class);

        UserDTO updateUser = userService.updateUser(id, userDto);
        returnValue = new ModelMapper().map(updateUser, UserRest.class);

        return returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @DeleteMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page, //Pages starts with 0!!!
                                   @RequestParam(value = "limit", defaultValue = "50") int limit) {

        List<UserRest> returnValue;

        List<UserDTO> users = userService.getUsers(page, limit);

        Type listType = new TypeToken<List<UserRest>>() {
        }.getType();
        returnValue = new ModelMapper().map(users, listType);

        return returnValue;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "/{userId}/addresses", produces = {MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE, "application/hal+json"})
    public CollectionModel<AddressRest> getUserAddresses(@PathVariable String userId) {

        List<AddressRest> addressRestList = new ArrayList<>();

        List<AddressDTO> addressesDTO = addressService.getAddresses(userId);

        if (addressesDTO != null && !addressesDTO.isEmpty()) {
            Type listType = new TypeToken<List<AddressRest>>() {
            }.getType();
            addressRestList = new ModelMapper().map(addressesDTO, listType);

            for (AddressRest addressRest : addressRestList) {
                Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressRest.getAddressId())).withSelfRel();
                Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");

                addressRest.add(addressLink).add(userLink);
            }
        }

        return new CollectionModel<>(addressRestList);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "authorization", value = "${userController.authorizationHeader.description}", paramType = "header")
    })
    @GetMapping(path = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "application/hal+json"})
    public EntityModel<AddressRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

        AddressDTO addressesDTO = addressService.getAddress(addressId);
        ModelMapper modelMapper = new ModelMapper();

        Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId, addressId)).withSelfRel();
        Link userLink = linkTo(UserController.class).slash(userId).withRel("user");
        Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("addresses");


        AddressRest addressRest = modelMapper.map(addressesDTO, AddressRest.class);
        addressRest
                .add(addressLink)
                .add(userLink)
                .add(addressesLink);

        return new EntityModel<>(addressRest);
    }

    @GetMapping(path = "/email-verification",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token") String token) {

        OperationStatusModel returnValue = new OperationStatusModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified = userService.verifyEmailToken(token);

        if (isVerified) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return returnValue;
    }

    @PostMapping(path = "/password-reset-request",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel requestResetPassword(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {

        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());

        if (operationResult) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return returnValue;
    }

    @PostMapping(path = "/password-reset",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {

        OperationStatusModel returnValue = new OperationStatusModel();

        boolean operationResult = userService.resetPassword(passwordResetModel.getToken(), passwordResetModel.getPassword());

        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());

        if (operationResult) {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        } else {
            returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
        }

        return returnValue;
    }
}
