package com.ag.domain.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRole {
    ROLE_NORMAL(0, "R"),
    NORMAL(1, "ROLE_NORMAL"),
    ADMIN(2,"ROLE_NORMAL"),
    ANONYMOUS(3,"ROLE_NORMAL");
    public final int id;
    private final String name;
}
