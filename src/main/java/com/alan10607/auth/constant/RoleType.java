package com.alan10607.auth.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RoleType {
    NORMAL(1),
    ADMIN(2),
    ANONYMOUS(3);
    public final int id;
}
