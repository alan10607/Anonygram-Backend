package com.alan10607.ag.service.forum;

import com.alan10607.ag.dao.ContLikeDAO;
import com.alan10607.ag.dto.LikeDTO;
import com.alan10607.ag.model.ContLike;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class LikeServiceTest {
    @InjectMocks
    private LikeService likeService;

    @Mock
    private ContLikeDAO contLikeDAO;

    private String id = "35bf88e3-4acb-4fe5-af16-60440d4915e5";
    private String id2 = "479199eb-864d-48a1-acb8-7c84866a70f6";
    private String userId = "ff80808189e387a90189e387cbf50000";
    private String userId2 = "fHcxQT42";

    @Test
    void valid_collectLikeMap_can_override() {
        Map<LikeDTO, Boolean> likeMap = new HashMap<>();
        Queue<LikeDTO> updateQueue = new LinkedList<>();
        updateQueue.offer(new LikeDTO(id, 0, userId, true));
        updateQueue.offer(new LikeDTO(id, 0, userId, false));
        updateQueue.offer(new LikeDTO(id, 1, userId, true));
        updateQueue.offer(new LikeDTO(id, 1, userId, false));
        updateQueue.offer(new LikeDTO(id2, 2, userId2, true));
        updateQueue.offer(new LikeDTO(id, 1, userId, true));
        updateQueue.offer(new LikeDTO(id2, 2, userId2, false));
        likeService.collectLikeMap(updateQueue, likeMap);

        assertEquals(false, likeMap.get(new LikeDTO(id, 0, userId)));
        assertEquals(true, likeMap.get(new LikeDTO(id, 1, userId)));
        assertEquals(false, likeMap.get(new LikeDTO(id2, 2, userId2)));
    }

    @Test
    void valid_collectEntityAndCount_can_count_like() {
        Map<LikeDTO, Boolean> likeMap = new HashMap<>();
        likeMap.put(new LikeDTO(id, 0, userId), true);
        likeMap.put(new LikeDTO(id, 1, userId), false);
        likeMap.put(new LikeDTO(id, 2, userId), true);
        likeMap.put(new LikeDTO(id, 3, userId), false);
        likeMap.put(new LikeDTO(id, 2, userId2), true);

        when(contLikeDAO.findByIdAndNoAndUserId(id, 0, userId)).thenReturn(new ContLike(id, 0, userId));
        when(contLikeDAO.findByIdAndNoAndUserId(id, 1, userId)).thenReturn(new ContLike(id, 1, userId));
        when(contLikeDAO.findByIdAndNoAndUserId(id, 2, userId)).thenReturn(null);
        when(contLikeDAO.findByIdAndNoAndUserId(id, 3, userId)).thenReturn(null);
        when(contLikeDAO.findByIdAndNoAndUserId(id, 2, userId2)).thenReturn(null);

        List<ContLike> createEntities = new ArrayList<>();
        List<ContLike> deleteEntities = new ArrayList<>();
        Map<LikeDTO, Long> likeCount = new HashMap<>();
        likeService.collectEntityAndCount(likeMap, createEntities, deleteEntities, likeCount);

        assertEquals(new ContLike(id, 2, userId), createEntities.get(0));
        assertEquals(new ContLike(id, 2, userId2), createEntities.get(1));
        assertEquals(new ContLike(id, 1, userId), deleteEntities.get(0));
        assertEquals(2, likeCount.get(new LikeDTO(id, 2)));
        assertEquals(-1, likeCount.get(new LikeDTO(id, 1)));
    }

}