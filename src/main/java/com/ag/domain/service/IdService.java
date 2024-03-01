package com.ag.domain.service;

import com.ag.domain.repository.ArticleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class IdService {
    private final ArticleRepository articleRepository;

    public List<String> get() {
        return new ArrayList<>();
    }


}
