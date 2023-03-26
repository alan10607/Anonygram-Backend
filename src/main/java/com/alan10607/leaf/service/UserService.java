package com.alan10607.leaf.service;

import com.alan10607.leaf.constant.LeafRoleType;
import com.alan10607.leaf.dto.LeafUserDTO;
import com.alan10607.leaf.model.LeafRole;
import com.alan10607.leaf.model.LeafUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    LeafUserDTO login(String email, String pw);
    LeafUserDTO loginAnonymity();
    LeafUser getAnonymousUser(String userName);
    void register(String email, String userName, String pw);
    LeafUserDTO findUser(String email);
    List<LeafUserDTO> findAllUser();
    void createUser(String email, String userName, String pw, LeafRoleType roleType);
    void updateUserName(String email, String userName);
    void deleteUser(String email);
    LeafRole findRole(String roleName);
    void saveRole(LeafRole leafRole);
    String findUserNameFromRedis(String id);
    void deleteUserNameFromRedis(String id);
}