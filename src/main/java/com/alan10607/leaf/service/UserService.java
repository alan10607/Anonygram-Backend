package com.alan10607.leaf.service;

import com.alan10607.leaf.constant.LeafRoleType;
import com.alan10607.leaf.dto.LeafUserDTO;
import com.alan10607.leaf.model.LeafRole;
import com.alan10607.leaf.model.GramUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    LeafUserDTO findUser(String email);
    List<LeafUserDTO> findAllUser();
    void createUser(String email, String userName, String pw, LeafRoleType roleType);
    void updateUserName(String email, String userName);
    void deleteUser(String email);
    LeafRole findRole(String roleName);
    void saveRole(LeafRole leafRole);
    String findUserNameFromRedis(String id);
    void deleteUserNameFromRedis(String id);
    GramUser getAnonymousUser(String userName);
}