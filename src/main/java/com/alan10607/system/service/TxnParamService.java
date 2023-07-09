package com.alan10607.system.service;

import com.alan10607.system.constant.TxnParamKey;
import com.alan10607.system.dao.TxnParamDAO;
import com.alan10607.system.model.TxnParam;
import com.alan10607.leaf.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TxnParamService {
    private TxnParamDAO txnParamDAO;

    public String get(TxnParamKey key) {
        return txnParamDAO.findById(key.name())
                .map(TxnParam::getValue)
                .orElseThrow(() -> new IllegalStateException("Param not found"));
    }

    public void set(TxnParamKey key, String value) {
        txnParamDAO.save(new TxnParam(key.name(), value, TimeUtil.now()));
    }

    public void delete(TxnParamKey key) {
        txnParamDAO.findById(key.name()).ifPresentOrElse(
                txnParam -> txnParamDAO.delete(txnParam),
                () -> new IllegalStateException("Param not found"));
    }

}