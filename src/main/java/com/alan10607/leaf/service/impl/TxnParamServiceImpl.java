package com.alan10607.leaf.service.impl;

import com.alan10607.leaf.constant.TxnParamType;
import com.alan10607.leaf.dao.TxnParamDAO;
import com.alan10607.leaf.model.TxnParam;
import com.alan10607.leaf.service.TxnParamService;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TxnParamServiceImpl implements TxnParamService {
    private TxnParamDAO txnParamDAO;
    private final TimeUtil timeUtil;


    public String find(String key) {
        TxnParam txnParam = txnParamDAO.findById(key)
                .orElseThrow(() -> new IllegalStateException("Param not found"));

        return txnParam.getValue();
    }

    public void update(String key, String value) {
        txnParamDAO.save(new TxnParam(key, value, TxnParamType.STRING, timeUtil.now()));
    }

    public void update(String key, int value) {
        txnParamDAO.save(new TxnParam(key, Integer.toString(value), TxnParamType.INT, timeUtil.now()));
    }

    public void delete(String key) {
        TxnParam txnParam = txnParamDAO.findById(key)
                .orElseThrow(() -> new IllegalStateException("Param not found"));

        txnParamDAO.delete(txnParam);
    }

}