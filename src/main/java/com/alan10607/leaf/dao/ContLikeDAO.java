package com.alan10607.leaf.dao;

import com.alan10607.leaf.model.ContLike;
import com.alan10607.leaf.model.ContLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContLikeDAO extends JpaRepository<ContLike, ContLikeId> {
    ContLike findByIdAndNoAndUserId(String id, int no, String userId);
    boolean existsByIdAndNoAndUserId(String id, int no, String userId);


//    @Transactional
//    default <S extends ContLike> List<S> saveAllContLike(Iterable<S> entities) {
//        Assert.notNull(entities, "Entities must not be null!");
//        List<S> result = new ArrayList();
//        Iterator<S> var3 = entities.iterator();
//
//        while(var3.hasNext()) {
//            S entity = var3.next();
//            result.add(saveContent(entity));
//        }
//
//        return result;
//    }
//
//    @Transactional
//    default <S extends ContLike> S saveContent(S entity) {
//        Assert.notNull(entity, "Entity must not be null.");
//        if (this.entityInformation.isNew(entity)) {
//            this.em.persist(entity);
//            return entity;
//        } else {
//            return this.em.merge(entity);
//        }
//    }

}
