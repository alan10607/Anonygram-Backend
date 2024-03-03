package com.ag.domain.service.base;

import com.ag.domain.util.PojoFiledUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@AllArgsConstructor
@Slf4j
public abstract class CrudServiceImpl<Entity> implements CrudService<Entity> {

    public abstract Entity getImpl(Entity entity);

    public abstract Entity createImpl(Entity entity);

    public abstract Entity updateImpl(Entity entity);

    public abstract Entity patchImpl(Entity entity);

    public abstract Entity deleteImpl(Entity entity);

    @Override
    public Entity get(Entity entity) {
        this.beforeGet(entity);
        return getImpl(entity);
    }

    @Override
    public Entity create(Entity entity) {
        this.beforeCreate(entity);
        this.validateIsNotExist(entity);
        return createImpl(entity);
    }

    @Override
    public Entity update(Entity entity) {
        this.validateIsExist(entity);
        this.beforeUpdateAndPatch(entity);
        return updateImpl(entity);
    }

    @Override
    public Entity patch(Entity entity) {
        Entity oldEntity = this.validateIsExist(entity);
        PojoFiledUtil.overwritePublicFields(oldEntity, entity);
        this.beforeUpdateAndPatch(oldEntity);
        return patchImpl(oldEntity);
    }

    @Override
    public Entity delete(Entity entity) {
        Entity oldEntity = this.validateIsExist(entity);
        this.beforeDelete(oldEntity);
        return deleteImpl(oldEntity);
    }

    private Entity validateIsExist(Entity entity) {
        if (this.get(entity) == null) {
            throw new EntityNotFoundException("Entity not found in CRUD");
        }
        return entity;
    }

    private void validateIsNotExist(Entity entity) {
        if (this.get(entity) != null) {
            throw new EntityNotFoundException("Entity already found in CRUD");
        }
    }

    protected void beforeGet(Entity entity) {
    }

    protected void beforeCreate(Entity entity) {
    }

    protected void beforeUpdateAndPatch(Entity entity) {
    }

    protected void beforeDelete(Entity entity) {
    }

}
