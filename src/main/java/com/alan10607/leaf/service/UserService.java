package com.alan10607.leaf.service;

import com.alan10607.leaf.constant.LeafRoleType;
import com.alan10607.leaf.dto.LeafUserDTO;
import com.alan10607.leaf.model.LeafRole;
import com.alan10607.leaf.model.LeafUser;

import java.util.List;

public interface UserService {
    LeafUserDTO login(String email);
    LeafUserDTO loginAnony();
    LeafUser getAnonyUser(String userName);
    LeafUserDTO findUser(String email);
    List<LeafUserDTO> findAllUser();
    void createUser(String email, String userName, String pw, LeafRoleType roleType);
    void updateUserName(String email, String userName);
    void deleteUser(String email);
    void saveRole(LeafRole leafRole);
    String findUserNameFromRedis(String id);
    void deleteUserNameFromRedis(String id);
}