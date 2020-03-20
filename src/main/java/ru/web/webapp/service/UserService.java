package ru.web.webapp.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.web.webapp.shared.dto.UserDTO;

import java.util.List;

public interface UserService extends UserDetailsService {

    UserDTO createUser(UserDTO user);

    UserDTO getUser(String email);

    UserDTO getUserByUserId(String userId);

    UserDTO updateUser(String userId, UserDTO userDto);

    void deleteUser(String userId);

    List<UserDTO> getUsers(int page, int limit);

    boolean verifyEmailToken(String token);

    boolean requestPasswordReset(String email);

    boolean resetPassword(String token, String password);
}
