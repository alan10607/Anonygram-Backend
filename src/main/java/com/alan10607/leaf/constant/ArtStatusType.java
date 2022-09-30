package com.alan10607.leaf.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ArtStatusType {
    NEW("new"),
    NORMAL("normal"),
    DELETED("deleted");
    private String name;
}