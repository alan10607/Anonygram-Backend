package com.alan10607.leaf.service;

public interface TxnParamService {
    String find(String key);
    void update(String key, String value);
    void update(String key, int value);
    void delete(String key);
}