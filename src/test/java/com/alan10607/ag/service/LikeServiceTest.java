package com.alan10607.ag.service;

import com.alan10607.ag.service.forum.LikeService;
import com.alan10607.ag.dto.LikeDTO;
import com.alan10607.ag.service.redis.UpdateLikeRedisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LikeServiceTest {
    @InjectMocks
    private LikeService likeService;

    @Mock
    private UpdateLikeRedisService updateLikeRedisService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void saveLikeToDB() {
        List<LikeDTO> updateList = new ArrayList<>();
        updateList.add(new LikeDTO("a",0, "b"));

        Mockito.when(updateLikeRedisService.get())
                .thenReturn(updateList);

        int count = likeService.saveLikeToDB();
        assertEquals(count, 2);

    }
}