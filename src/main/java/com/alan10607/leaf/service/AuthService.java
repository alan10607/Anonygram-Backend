package com.alan10607.leaf.service;

import com.alan10607.leaf.dto.LeafUserDTO;

public interface AuthService {
    LeafUserDTO login(String email, String pw);
    LeafUserDTO loginAnonymity();
    void register(String email, String userName, String pw);
}





