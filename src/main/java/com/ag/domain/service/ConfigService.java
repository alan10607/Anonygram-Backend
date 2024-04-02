package com.ag.domain.service;

import com.ag.domain.model.base.ConfigEntity;
import com.ag.domain.repository.ConfigRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ConfigService<C extends ConfigEntity> {
    private final ConfigRepository<C> configRepository;

    public C get(ConfigEntity.Type type) {
        return configRepository.findById(type.id).orElse(null);
    }

    public C save(C c) {
        return configRepository.save(c);
    }
}
