package com.ag.domain.service;

import com.ag.domain.util.ObjectFieldUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@AllArgsConstructor
@Slf4j
public abstract class CRUDServiceImpl<Entity> implements CRUDService<Entity> {

    public abstract Entity getImpl(Entity entity);

    public abstract Entity createImpl(Entity entity);

    public abstract Entity updateImpl(Entity entity);

    public abstract Entity patchImpl(Entity entity);

    public abstract Entity deleteImpl(Entity entity);


    @Override
    public Entity get(Entity entity) {
        this.validateGet(entity);
        return getImpl(entity);
    }

    @Override
    public Entity create(Entity entity) {
        this.validateCreate(entity);
        return createImpl(entity);
    }

    @Override
    public Entity update(Entity entity) {
        this.validateExist(entity);
        this.validateUpdateAndPatch(entity);
        return updateImpl(entity);
    }

    @Override
    public Entity patch(Entity entity) {
        Entity oldEntity = this.validateExist(entity);
        ObjectFieldUtil.overwritePublicFields(oldEntity, entity);
        this.validateUpdateAndPatch(oldEntity);
        return patchImpl(oldEntity);
    }

    @Override
    public Entity delete(Entity entity) {
        Entity oldEntity = this.validateExist(entity);
        this.validateDelete(oldEntity);
        return deleteImpl(oldEntity);
    }

    private Entity validateExist(Entity entity) {
        if (this.get(entity) == null) {
            throw new EntityNotFoundException("Entity not found in CRUD");
        }
        return entity;
    }

    protected void validateGet(Entity entity) {
    }

    protected void validateCreate(Entity entity) {
    }

    protected void validateUpdateAndPatch(Entity entity) {
    }

    protected void validateDelete(Entity entity) {
    }

}
