package com.alan10607.leaf.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum StatusType {
    NORMAL("normal"),
    DELETED("deleted"),
    UNKNOWN("unknown");
    public final String value;
}