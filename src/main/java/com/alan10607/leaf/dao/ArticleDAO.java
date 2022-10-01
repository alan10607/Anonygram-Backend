package com.alan10607.leaf.dao;

import com.alan10607.leaf.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleDAO extends JpaRepository<Article, String> {
    Optional<Article> findById(String id);
    List<Article> findByIdIn(List<String> ids);

    @Query(nativeQuery = true, value = "SELECT a.id FROM Article a WHERE a.createDate > (NOW() - INTERVAL 30 DAY) ORDER BY a.updateDate DESC LIMIT 100")
    List<String> findLatest100Id();

    @Query(nativeQuery = true, value = "SELECT a.contNum FROM Article WHERE a.id = ?1 LOCK IN SHARE MODE")
    Optional<Integer> findContNumByIdWithLock(String id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Article a SET a.contNum = a.contNum + 1 WHERE a.id = ?1")
    int incrContNum(String id);


//
//    @Transactional
//    public default <S extends T> Iterable<S> batchSave(Iterable<S> var1) {
//        Iterator<S> iterator = var1.iterator();
//        int index = 0;
//        while (iterator.hasNext()){
//            em.persist(iterator.next());
//            index++;
//            if (index % BATCH_SIZE == 0){
//                em.flush();
//                em.clear();
//            }
//        }
//        if (index % BATCH_SIZE != 0){
//            em.flush();
//            em.clear();
//        }
//        return var1;
//    }
}