package com.alan10607.leaf.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum StatusType {
    NEW("new"),
    NORMAL("normal"),
    DELETED("deleted"),
    UNKNOWN("unknown");
    public String value;
}