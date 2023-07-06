package com.alan10607.auth.service;

import com.alan10607.auth.dao.RoleDAO;
import com.alan10607.auth.model.Role;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@AllArgsConstructor
@Slf4j
@Validated
public class RoleService {
    private final RoleDAO roleDAO;

    public Role findRole(String roleName) {
        return roleDAO.findByRoleName(roleName);
    }

    public void saveRole(Role role) {
        roleDAO.save(role);
    }

}