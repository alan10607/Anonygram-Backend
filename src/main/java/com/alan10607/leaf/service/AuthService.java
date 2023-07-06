package com.alan10607.leaf.service;

import com.alan10607.auth.dto.UserDTO;

public interface AuthService {
    UserDTO login(String email, String pw);
    UserDTO loginAnonymity();
    void register(String email, String userName, String pw);
}





