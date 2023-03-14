package com.beyonder.authservice.service;

import com.beyonder.authservice.dto.UserDTO;
import org.springframework.security.core.Authentication;

public interface TokenService {
    String generatedToken(Authentication authentication);
    UserDTO decodeToken(String token);
    void addUser(UserDTO userDTO);
}
